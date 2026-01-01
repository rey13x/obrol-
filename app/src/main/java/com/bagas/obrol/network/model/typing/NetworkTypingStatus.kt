package com.bagas.obrol.network.model.typing

import kotlinx.serialization.Serializable

@Serializable
data class NetworkTypingStatus(
    val senderId: Long,
    val receiverId: Long,
    val isTyping: Boolean
)
