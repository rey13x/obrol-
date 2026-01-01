package com.bagas.obrol.network.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bagas.obrol.App
import com.bagas.obrol.R
import com.bagas.obrol.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkService : Service() {

    private lateinit var networkManager: NetworkManager

    override fun onCreate() {
        super.onCreate()
        networkManager = (application as App).container.networkManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Obrol Aktif")
            .setContentText("Menjalankan koneksi untuk menerima pesan.")
            .setSmallIcon(R.drawable.obrol)
            .build()

        startForeground(1, notification)

        CoroutineScope(Dispatchers.IO).launch {
            networkManager.startConnections()
            networkManager.startDiscoverPeersHandler()
            networkManager.startSendKeepaliveHandler()
            networkManager.startUpdateConnectedDevicesHandler()
        }
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Koneksi Obrol",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        private const val NOTIFICATION_CHANNEL_ID = "obrol_network_channel"
    }
}