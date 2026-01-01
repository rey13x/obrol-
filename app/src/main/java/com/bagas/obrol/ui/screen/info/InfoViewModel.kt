package com.bagas.obrol.ui.screen.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagas.obrol.domain.model.message.FileMessage
import com.bagas.obrol.domain.model.message.TextMessage
import com.bagas.obrol.domain.repository.ChatRepository
import com.bagas.obrol.domain.repository.ContactRepository
import com.bagas.obrol.network.NetworkManager
import com.bagas.obrol.ui.components.getMimeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InfoViewModel(
    private val chatRepository: ChatRepository,
    private val contactRepository: ContactRepository,
    val networkManager: NetworkManager,
    private val accountId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(InfoViewState())
    val uiState: StateFlow<InfoViewState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            contactRepository.getContactByAccountIdAsFlow(accountId).collect { contact ->
                if(contact?.profile != null) {
                    _uiState.value = contact.profile.let { _uiState.value.copy(profile = it) }
                }
            }
        }

        viewModelScope.launch {
            chatRepository.getAllMessagesByAccountIdAsFlow(accountId).collect { messages ->
                val imageMessages = messages.filterIsInstance<FileMessage>().filter { getMimeType(it.fileName).startsWith("image/") }
                val linkMessages = messages.filterIsInstance<TextMessage>().filter { it.text.contains("http://") || it.text.contains("https://") }
                val fileMessages = messages.filterIsInstance<FileMessage>().filter { !getMimeType(it.fileName).startsWith("image/") }

                _uiState.value = _uiState.value.copy(
                    imageMessages = imageMessages,
                    linkMessages = linkMessages,
                    fileMessages = fileMessages
                )
            }
        }
    }
}