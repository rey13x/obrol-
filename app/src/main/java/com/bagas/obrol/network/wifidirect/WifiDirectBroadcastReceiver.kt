package com.bagas.obrol.network.wifidirect

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.bagas.obrol.MainActivity
import com.bagas.obrol.network.NetworkManager

@SuppressLint("MissingPermission")
class WiFiDirectBroadcastReceiver : BroadcastReceiver() {

    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var networkManager: NetworkManager
    private lateinit var activity: MainActivity

    companion object {
        const val IP_GROUP_OWNER: String = "192.168.49.1"
    }

    fun init(manager: WifiP2pManager, channel: WifiP2pManager.Channel, networkManager: NetworkManager, activity: MainActivity) {
        this.manager = manager
        this.channel = channel
        this.networkManager = networkManager
        this.activity = activity
    }

    fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("WifiDirectBroadcastReceiver", "Discovery initiated")
            }

            override fun onFailure(reasonCode: Int) {
                Log.d("WifiDirectBroadcastReceiver", "Discovery failed: $reasonCode")
            }
        })
    }

    private fun connectToDevices() {
        manager.requestPeers(channel) { devices ->
            Log.d("WifiDirectBroadcastReceiver", "Peers found: ${devices.deviceList.size}")
            for(device in devices.deviceList) {
                // Hanya hubungkan jika belum terhubung atau status tersedia
                if (device.status == WifiP2pDevice.AVAILABLE) {
                    connectToDevice(device)
                }
            }
        }
    }

    private fun connectToDevice(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            groupOwnerIntent = 0 // Prefer to be a client to avoid breaking existing groups
        }
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("WifiDirectBroadcastReceiver", "Connecting to ${device.deviceName}")
            }

            override fun onFailure(reason: Int) {
                Log.d("WifiDirectBroadcastReceiver", "Failed to connect to ${device.deviceName}")
            }
        })
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.d("WifiDirectBroadcastReceiver", "Wi-Fi P2P enabled")
                } else {
                    Log.d("WifiDirectBroadcastReceiver", "Wi-Fi P2P disabled")
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                Log.d("WifiDirectBroadcastReceiver", "Peers changed")
                connectToDevices()
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Log.d("WifiDirectBroadcastReceiver", "Connection changed")
                manager.requestConnectionInfo(channel) { info ->
                    if (info.groupFormed) {
                        if (info.isGroupOwner) {
                            Log.d("WifiDirectBroadcastReceiver", "This device is the group owner")
                        } else {
                            Log.d("WifiDirectBroadcastReceiver", "This device is a client")
                        }
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                Log.d("WifiDirectBroadcastReceiver", "This device changed")
            }
        }
    }
}