package com.bagas.obrol.domain.model.device

import androidx.compose.runtime.Immutable
import com.bagas.obrol.data.local.account.AccountEntity
import com.bagas.obrol.network.model.device.NetworkAccount

@Immutable
data class Account(
    val accountId: Long,
    val profileUpdateTimestamp: Long
)

fun Account.toAccountEntity(): AccountEntity {
    return AccountEntity(
        accountId = accountId,
        profileUpdateTimestamp = profileUpdateTimestamp
    )
}

fun AccountEntity.toAccount(): Account {
    return Account(
        accountId = accountId,
        profileUpdateTimestamp = profileUpdateTimestamp
    )
}

fun NetworkAccount.toAccount(): Account {
    return Account(
        accountId = accountId,
        profileUpdateTimestamp = profileUpdateTimestamp
    )
}

fun Account.toNetworkAccount(): NetworkAccount {
    return NetworkAccount(
        accountId = accountId,
        profileUpdateTimestamp = profileUpdateTimestamp
    )
}
