package com.bagas.obrol.ui.screen.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.bagas.obrol.App

class CallViewModelFactory(private val accountId: Long, private val callState: String) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(CallViewModel::class.java)) {
            val application = (extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)

            return CallViewModel(
                contactRepository = application.container.contactRepository,
                callManager = application.container.callManager,
                networkManager = application.container.networkManager,
                accountId = accountId,
                initialCallState = callState
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
