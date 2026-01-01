package com.bagas.obrol.domain.repository

import com.bagas.obrol.domain.model.device.Account
import kotlinx.coroutines.flow.Flow

interface OwnAccountRepository {
    fun getAccountAsFlow(): Flow<Account>
    suspend fun getAccount(): Account
    suspend fun setAccountId(accountId: Long)
    suspend fun setProfileUpdateTimestamp(profileUpdateTimestamp: Long)
}