package com.bagas.obrol.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bagas.obrol.App
import com.bagas.obrol.ui.screen.home.HomeViewModel
import com.bagas.obrol.ui.screen.servicechat.ServiceChatViewModel
import com.bagas.obrol.ui.screen.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).container.chatRepository,
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).container.networkManager
            )
        }

        initializer {
            SettingsViewModel(
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).container.ownAccountRepository,
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).container.ownProfileRepository,
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).container.fileManager,
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).container.networkManager
            )
        }

        initializer {
            ServiceChatViewModel(
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App).container.ownProfileRepository
            )
        }

        // CallViewModel is not created here because it requires navigation arguments.
        // It is created via its own factory (CallViewModelFactory) in ObrolNavGraph.kt.
    }
}
