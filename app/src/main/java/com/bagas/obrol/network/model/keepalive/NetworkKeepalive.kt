package com.bagas.obrol.network.model.keepalive

import com.bagas.obrol.network.model.device.NetworkDevice
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkKeepalive(
    val networkDevices: List<NetworkDevice>
)