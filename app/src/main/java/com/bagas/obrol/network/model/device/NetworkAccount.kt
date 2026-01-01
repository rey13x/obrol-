package com.bagas.obrol.network.model.device

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkAccount(
    val accountId: Long,
    val profileUpdateTimestamp: Long
)