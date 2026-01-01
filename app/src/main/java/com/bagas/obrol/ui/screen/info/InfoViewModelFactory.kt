package com.bagas.obrol.ui.screen.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.bagas.obrol.App

class InfoViewModelFactory(private val accountId: Long) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(InfoViewModel::class.java)) {
            val application = (extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)

            return InfoViewModel(
                application.container.chatRepository,
                application.container.contactRepository,
                application.container.networkManager,
                accountId
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}