package com.bagas.obrol.network.model.message

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkMessageAck(
    val messageId: Long,
    val senderId: Long,
    val receiverId: Long
)