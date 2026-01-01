package com.bagas.obrol.ui.screen.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagas.obrol.domain.repository.ContactRepository
import com.bagas.obrol.network.CallManager
import com.bagas.obrol.network.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class CallViewModel(
    private val contactRepository: ContactRepository,
    private val callManager: CallManager,
    val networkManager: NetworkManager,
    private val accountId: Long,
    private val initialCallState: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(CallViewState())
    val uiState: StateFlow<CallViewState> = _uiState.asStateFlow()

    private val isCalling = AtomicBoolean(false)

    init {
        // Set initial call state from navigation argument
        val callState = when (initialCallState) {
            "outgoing" -> CallState.SENT_CALL_REQUEST
            "incoming" -> CallState.RECEIVED_CALL_REQUEST
            else -> null
        }
        _uiState.value = _uiState.value.copy(callState = callState)

        // Load contact information
        viewModelScope.launch {
            contactRepository.getContactByAccountIdAsFlow(accountId).collect { contact ->
                if (contact != null) {
                    _uiState.value = _uiState.value.copy(contact = contact)
                }
            }
        }

        // Listen for incoming audio fragments
        viewModelScope.launch {
            networkManager.callFragment.collect { audioBytes ->
                audioBytes?.let {
                    callManager.playAudio(it)
                }
            }
        }
    }

    fun sendCallEnd() {
        uiState.value.contact?.account?.accountId?.let {
            networkManager.sendCallEnd(it)
        }
    }

    fun acceptCall() {
        networkManager.resetCallStateFlows()
        uiState.value.contact?.account?.accountId?.let {
            networkManager.sendCallResponse(it, true)
        }
    }

    fun declineCall() {
        networkManager.resetCallStateFlows()
        uiState.value.contact?.account?.accountId?.let {
            networkManager.sendCallResponse(it, false)
        }
    }

    fun startCall() {
        networkManager.resetCallStateFlows()
        startCallSession()
    }

    fun endCall() {
        networkManager.resetCallStateFlows()
        endCallSession()
    }

    private fun startCallSession() {
        if (!isCalling.get()) {
            try {
                callManager.startPlaying()
                callManager.startRecording { audioBytes ->
                    uiState.value.contact?.account?.accountId?.let {
                        networkManager.sendCallFragment(it, audioBytes)
                    }
                }
                _uiState.value = _uiState.value.copy(callState = CallState.CALL)
                isCalling.set(true)
            } catch (_: Exception) {
                // Handle exceptions
            }
        }
    }

    private fun endCallSession() {
        if (isCalling.get()) {
            viewModelScope.launch {
                callManager.stopPlaying()
                callManager.stopRecording()
                isCalling.set(false)
            }
        }
    }

    fun setSpeakerOn() {
        _uiState.value = _uiState.value.copy(isSpeakerOn = true)
        callManager.enableSpeakerVolume()
    }

    fun setSpeakerOff() {
        _uiState.value = _uiState.value.copy(isSpeakerOn = false)
        callManager.disableSpeakerVolume()
    }
}