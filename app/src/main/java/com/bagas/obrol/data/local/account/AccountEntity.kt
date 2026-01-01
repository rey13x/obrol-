package com.bagas.obrol.data.local.account

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
@Entity
data class AccountEntity(
    @PrimaryKey
    val accountId: Long,
    val profileUpdateTimestamp: Long
)
