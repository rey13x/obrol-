package com.bagas.obrol.network.model.call

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkCallResponse(
    val senderId: Long,
    val receiverId: Long,
    val accepted: Boolean
)