package com.bagas.obrol.ui.screen.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagas.obrol.data.local.FileManager
import com.bagas.obrol.domain.model.device.Account
import com.bagas.obrol.domain.model.message.Message
import com.bagas.obrol.domain.model.message.MessageState
import com.bagas.obrol.domain.repository.ChatRepository
import com.bagas.obrol.domain.repository.ContactRepository
import com.bagas.obrol.domain.repository.OwnAccountRepository
import com.bagas.obrol.media.AudioReplayer
import com.bagas.obrol.network.NetworkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File

sealed class ChatEvent {
    data class NavigateToCall(val accountId: Long) : ChatEvent()
}

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val contactRepository: ContactRepository,
    private val ownAccountRepository: OwnAccountRepository,
    private val fileManager: FileManager,
    val networkManager: NetworkManager,
    private val accountId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatViewState())
    val uiState: StateFlow<ChatViewState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ChatEvent>()
    val events = _events.asSharedFlow()

    val ownAccount: Flow<Account>
        get() = ownAccountRepository.getAccountAsFlow()

    private val audioReplayer = AudioReplayer()

    init {
        listenToContact()
        listenToMessages()
        listenToNetworkEvents()
    }

    private fun listenToContact() {
        viewModelScope.launch {
            contactRepository.getContactByAccountIdAsFlow(accountId).collect { contact ->
                if (contact != null) {
                    _uiState.value = _uiState.value.copy(contact = contact)
                }
            }
        }
    }

    private fun listenToMessages() {
        viewModelScope.launch {
            chatRepository.getAllMessagesByAccountIdAsFlow(accountId).collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    private fun listenToNetworkEvents() {
        viewModelScope.launch {
            combine(
                networkManager.connectedDevices,
                networkManager.typingStatus
            ) { devices, typingStatus ->
                val isOpponentOnline = devices.any { it.account.accountId == accountId }
                val isOpponentTyping = typingStatus?.let {
                    it.senderId == accountId && it.isTyping
                } ?: false
                isOpponentOnline to isOpponentTyping
            }.collect { (isOpponentOnline, isOpponentTyping) ->
                _uiState.value = _uiState.value.copy(
                    isOpponentOnline = isOpponentOnline,
                    isOpponentTyping = isOpponentTyping
                )
            }
        }
    }

    fun sendTextMessage(text: String) {
        networkManager.sendTextMessage(accountId, text)
    }

    fun sendFileMessage(fileUri: Uri) {
        networkManager.sendFileMessage(accountId, fileUri)
    }

    fun sendAudioMessage() {
        audioReplayer.recordingFile?.let {
            networkManager.sendAudioMessage(accountId, it)
        }
    }

    fun sendCallRequest() {
        viewModelScope.launch {
            networkManager.sendCallRequest(accountId)
            _events.emit(ChatEvent.NavigateToCall(accountId))
        }
    }

    fun startRecordingAudio() = audioReplayer.startRecording(fileManager.getAudioTempFile())
    fun stopRecordingAudio() = audioReplayer.stopRecording()

    fun startPlayingAudio(file: File, startPosition: Int) {
        audioReplayer.startPlaying(file, startPosition)
    }

    fun stopPlayingAudio() = audioReplayer.stopPlaying()

    fun getCurrentPlaybackPosition(): Int {
        return audioReplayer.getCurrentPlaybackPosition()
    }

    fun isPlaybackComplete(): Boolean {
        return audioReplayer.isPlaybackComplete()
    }

    fun updateMessagesState(messages: List<Message>) {
        viewModelScope.launch {
            val ownAccountId = ownAccountRepository.getAccount().accountId

            val receivedMessages = messages.filter { it.messageState < MessageState.MESSAGE_READ && it.senderId != ownAccountId }

            receivedMessages.forEach { message ->
                chatRepository.updateMessageState(message.messageId, MessageState.MESSAGE_READ)
            }

            receivedMessages.lastOrNull()?.let { message ->
                networkManager.sendMessageReadAck(message.senderId, message.messageId)
            }
        }
    }

    fun sendTypingStatus(isTyping: Boolean) {
        networkManager.sendTypingStatus(accountId, isTyping)
    }
}