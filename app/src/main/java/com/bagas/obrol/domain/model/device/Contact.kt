package com.bagas.obrol.domain.model.device

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class Contact(
    val account: Account,val profile: Profile,
    val isServiceContact: Boolean = false
) : Serializable {

    companion object {
        fun createServiceContact(): Contact {
            return Contact(
                account = Account(
                    accountId = -1L,
                    profileUpdateTimestamp = 0L
                ),
                profile = Profile(
                    accountId = -1L,
                    updateTimestamp = 0L,
                    username = "Obrol+",
                    imageFileName = null
                ),
                isServiceContact = true
            )
        }
    }
}
