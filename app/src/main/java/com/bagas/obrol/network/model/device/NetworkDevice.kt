package com.bagas.obrol.network.model.device

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkDevice(
    val ipAddress: String?,
    val keepalive: Long,
    val account: NetworkAccount
)