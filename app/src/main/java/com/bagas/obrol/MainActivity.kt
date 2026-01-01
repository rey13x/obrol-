package com.bagas.obrol

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bagas.obrol.domain.repository.OwnAccountRepository
import com.bagas.obrol.domain.repository.OwnProfileRepository
import com.bagas.obrol.network.NetworkManager
import com.bagas.obrol.network.service.NetworkService
import com.bagas.obrol.ui.ObrolScreen
import com.bagas.obrol.ui.theme.ObrolTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var networkManager: NetworkManager

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            startNetworkService()
        } else {
            Toast.makeText(this, "Izin lokasi diperlukan untuk menemukan perangkat lain.", Toast.LENGTH_LONG).show()
        }
    }

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as App).container
        networkManager = appContainer.networkManager

        // Initialize NetworkManager and its receiver with dependencies from the Activity
        networkManager.init(this)

        lifecycleScope.launch {
            if (appContainer.ownAccountRepository.getAccount().accountId == 0L) {
                val id = Build.MODEL.hashCode().toLong()
                appContainer.ownAccountRepository.setAccountId(id)
                appContainer.ownProfileRepository.setAccountId(id)
            }
        }

        checkAndRequestPermissions()
        checkServices()

        setContent {
            ObrolTheme {
                ObrolScreen()
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            startNetworkService()
        } else {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun startNetworkService() {
        Intent(applicationContext, NetworkService::class.java).also {
            it.action = NetworkService.ACTION_START
            startService(it)
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(networkManager.receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkManager.receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }

    private fun checkServices() {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "Tolong aktifkan Wi-Fi", Toast.LENGTH_LONG).show()
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Tolong aktifkan lokasi", Toast.LENGTH_LONG).show()
        }
    }
}