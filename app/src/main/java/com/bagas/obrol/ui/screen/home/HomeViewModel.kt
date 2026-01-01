package com.bagas.obrol.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagas.obrol.domain.model.chat.ChatPreview
import com.bagas.obrol.domain.model.device.Contact
import com.bagas.obrol.domain.repository.ChatRepository
import com.bagas.obrol.network.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val chatPreviews: List<ChatPreview> = emptyList(),
    val onlineChats: Set<Long> = emptySet()
)

class HomeViewModel(
    private val chatRepository: ChatRepository,
    val networkManager: NetworkManager, // networkManager is kept for other potential uses
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // HANYA MENGAMBIL DATA DARI DATABASE LOKAL UNTUK MENJAMIN STABILITAS
            chatRepository.getAllChatPreviewsAsFlow().collect { previews ->
                val currentPreviews = previews.toMutableList()

                val serviceContactPreview = ChatPreview(
                    contact = Contact.createServiceContact(),
                    lastMessage = null,
                    unreadMessagesCount = 0
                )

                // Memastikan service contact ada di daftar
                val serviceContactExists = currentPreviews.any { it.contact.account.accountId == -1L }
                if (!serviceContactExists) {
                    currentPreviews.add(0, serviceContactPreview)
                }

                // Memperbarui UI hanya dengan data yang aman dari database
                _uiState.value = HomeState(
                    chatPreviews = currentPreviews,
                    onlineChats = emptySet() // Fitur online dinonaktifkan untuk mencegah crash
                )
            }
        }
    }
}
