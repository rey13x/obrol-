package com.bagas.obrol.data.local.profile

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
@Entity
data class ProfileEntity(
    @PrimaryKey
    val accountId: Long,
    val updateTimestamp: Long,
    val username: String,
    val imageFileName: String?
)