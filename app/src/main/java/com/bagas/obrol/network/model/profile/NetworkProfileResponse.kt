package com.bagas.obrol.network.model.profile

import com.bagas.obrol.network.model.device.NetworkProfile
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkProfileResponse(
    val senderId: Long,
    val receiverId: Long,
    val profile: NetworkProfile
)