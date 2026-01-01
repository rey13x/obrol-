package com.bagas.obrol.network.model.profile

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkProfileRequest(
    val senderId: Long,
    val receiverId: Long
)