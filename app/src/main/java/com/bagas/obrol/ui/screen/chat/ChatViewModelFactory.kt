package com.bagas.obrol.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.bagas.obrol.App

class ChatViewModelFactory(private val accountId: Long) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            val application = (extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)

            return ChatViewModel(
                application.container.chatRepository,
                application.container.contactRepository,
                application.container.ownAccountRepository,
                application.container.fileManager,
                application.container.networkManager,
                accountId
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}