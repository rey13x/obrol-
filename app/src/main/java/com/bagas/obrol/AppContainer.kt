package com.bagas.obrol

import android.content.Context
import com.bagas.obrol.data.local.AppDataStore
import com.bagas.obrol.data.local.AppDatabase
import com.bagas.obrol.data.local.FileManager
import com.bagas.obrol.data.repository.ChatLocalRepository
import com.bagas.obrol.data.repository.ContactLocalRepository
import com.bagas.obrol.data.repository.OwnAccountLocalRepository
import com.bagas.obrol.data.repository.OwnProfileLocalRepository
import com.bagas.obrol.domain.repository.ChatRepository
import com.bagas.obrol.domain.repository.ContactRepository
import com.bagas.obrol.domain.repository.OwnAccountRepository
import com.bagas.obrol.domain.repository.OwnProfileRepository
import com.bagas.obrol.network.CallManager
import com.bagas.obrol.network.NetworkManager
import com.bagas.obrol.network.wifidirect.WiFiDirectBroadcastReceiver
import com.bagas.obrol.ui.NotificationManager

interface AppContainer {
    val context: Context
    val handlerFactory: HandlerFactory
    val wiFiDirectBroadcastReceiver: WiFiDirectBroadcastReceiver
    val chatRepository: ChatRepository
    val contactRepository: ContactRepository
    val ownAccountRepository: OwnAccountRepository
    val ownProfileRepository: OwnProfileRepository
    val fileManager: FileManager
    val networkManager: NetworkManager
    val callManager: CallManager
    val notificationManager: NotificationManager
}

class DefaultAppContainer(override val context: Context) : AppContainer {

    override val handlerFactory = HandlerFactory(context)

    override val wiFiDirectBroadcastReceiver = WiFiDirectBroadcastReceiver()

    override val chatRepository: ChatRepository by lazy {
        ChatLocalRepository(AppDatabase.getDatabase(context).accountDao(), AppDatabase.getDatabase(context).messageDao(), AppDatabase.getDatabase(context).profileDao())
    }

    override val contactRepository: ContactRepository by lazy {
        ContactLocalRepository(AppDatabase.getDatabase(context).accountDao(), AppDatabase.getDatabase(context).profileDao())
    }

    override val ownAccountRepository: OwnAccountRepository by lazy {
        OwnAccountLocalRepository(AppDataStore.getAccountRepository(context))
    }

    override val ownProfileRepository: OwnProfileRepository by lazy {
        OwnProfileLocalRepository(AppDataStore.getProfileRepository(context))
    }

    override val fileManager: FileManager by lazy {
        FileManager(context)
    }

    override val notificationManager: NotificationManager by lazy {
        NotificationManager(context)
    }

    override val networkManager: NetworkManager by lazy {
        NetworkManager(ownAccountRepository, ownProfileRepository, handlerFactory, wiFiDirectBroadcastReceiver, chatRepository, contactRepository, fileManager, notificationManager)
    }

    override val callManager: CallManager by lazy {
        CallManager(context)
    }
}