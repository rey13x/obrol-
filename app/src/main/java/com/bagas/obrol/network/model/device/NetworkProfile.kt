package com.bagas.obrol.network.model.device

import com.bagas.obrol.domain.model.device.Profile
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkProfile(
    val accountId: Long,
    val updateTimestamp: Long,
    val username: String,
    val imageBase64: String?
) {
    constructor(profile: Profile, imageBase64: String?)
            : this(profile.accountId, profile.updateTimestamp, profile.username, imageBase64)
}