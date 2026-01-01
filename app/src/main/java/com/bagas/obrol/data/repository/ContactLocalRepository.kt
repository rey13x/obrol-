package com.bagas.obrol.data.repository

import com.bagas.obrol.data.local.account.AccountDAO
import com.bagas.obrol.data.local.profile.ProfileDAO
import com.bagas.obrol.domain.model.device.Account
import com.bagas.obrol.domain.model.device.Contact
import com.bagas.obrol.domain.model.device.Profile
import com.bagas.obrol.domain.model.device.toAccount
import com.bagas.obrol.domain.model.device.toAccountEntity
import com.bagas.obrol.domain.model.device.toProfile
import com.bagas.obrol.domain.model.device.toProfileEntity
import com.bagas.obrol.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ContactLocalRepository(private val accountDAO: AccountDAO, private val profileDAO: ProfileDAO) : ContactRepository {
    override fun getAllContactsAsFlow(): Flow<List<Contact>> {
        val accountsFlow = accountDAO.getAllAccountsAsFlow().map { accountEntities ->
            accountEntities.map { it.toAccount() }
        }

        val profilesFlow = profileDAO.getAllProfilesAsFlow().map { profileEntities ->
            profileEntities.map { it.toProfile() }
        }

        return combine(accountsFlow, profilesFlow) { accounts, profiles ->
            accounts.mapNotNull { account ->
                profiles.find { it.accountId == account.accountId }?.let {
                    Contact(account, it)
                }
            }
        }
    }

    override fun getContactByAccountIdAsFlow(accountId: Long): Flow<Contact?> {
        val accountFlow = accountDAO.getAccountByAccountIdAsFlow(accountId).map { accountEntity ->
            accountEntity?.toAccount()
        }

        val profileFlow = profileDAO.getProfileByAccountIdAsFlow(accountId).map { profileEntity ->
            profileEntity?.toProfile()
        }

        return combine(accountFlow, profileFlow) { account, profile ->
            if(account != null && profile != null) {
                Contact(account, profile)
            } else {
                null
            }
        }
    }

    override fun getAllAccountsAsFlow(): Flow<List<Account>> {
        return accountDAO.getAllAccountsAsFlow().map { accountEntities ->
            accountEntities.map { it.toAccount() }
        }
    }

    override fun getAccountByAccountIdAsFlow(accountId: Long): Flow<Account?> {
        return accountDAO.getAccountByAccountIdAsFlow(accountId).map { it?.toAccount() }
    }

    override suspend fun getAllAccounts(): List<Account> {
        return accountDAO.getAllAccounts().map { it.toAccount() }
    }

    override suspend fun getAccountByAccountId(accountId: Long): Account? {
        return accountDAO.getAccountByAccountId(accountId)?.toAccount()
    }

    override suspend fun addAccount(account: Account): Long {
        return accountDAO.insertAccount(account.toAccountEntity())
    }

    override suspend fun addOrUpdateAccount(account: Account): Long {
        return accountDAO.insertOrUpdateAccount(account.toAccountEntity())
    }

    override suspend fun updateAccount(account: Account) {
        accountDAO.updateAccount(account.toAccountEntity())
    }

    override suspend fun deleteAccount(account: Account) {
        accountDAO.deleteAccount(account.toAccountEntity())
    }

    override fun getAllProfilesAsFlow(): Flow<List<Profile>> {
        return profileDAO.getAllProfilesAsFlow().map { profileEntities ->
            profileEntities.map { it.toProfile() }
        }
    }

    override fun getProfileByAccountIdAsFlow(accountId: Long): Flow<Profile?> {
        return profileDAO.getProfileByAccountIdAsFlow(accountId).map { it?.toProfile() }
    }

    override suspend fun getAllProfiles(): List<Profile> {
        return profileDAO.getAllProfiles().map { it.toProfile() }
    }

    override suspend fun getProfileByAccountId(accountId: Long): Profile? {
        return profileDAO.getProfileByAccountId(accountId)?.toProfile()
    }

    override suspend fun addProfile(profile: Profile): Long {
        return profileDAO.insertProfile(profile.toProfileEntity())
    }

    override suspend fun addOrUpdateProfile(profile: Profile): Long {
        return profileDAO.insertOrUpdateProfile(profile.toProfileEntity())
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDAO.updateProfile(profile.toProfileEntity())
    }

    override suspend fun deleteProfile(profile: Profile) {
        profileDAO.deleteProfile(profile.toProfileEntity())
    }

}