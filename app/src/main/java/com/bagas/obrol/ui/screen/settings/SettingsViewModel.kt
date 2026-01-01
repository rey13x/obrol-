package com.bagas.obrol.ui.screen.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagas.obrol.data.local.FileManager
import com.bagas.obrol.domain.repository.OwnAccountRepository
import com.bagas.obrol.domain.repository.OwnProfileRepository
import com.bagas.obrol.network.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val ownAccountRepository: OwnAccountRepository,
    private val ownProfileRepository: OwnProfileRepository,
    private val fileManager: FileManager,
    val networkManager: NetworkManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsViewState())
    val uiState: StateFlow<SettingsViewState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            ownProfileRepository.getProfileAsFlow().collect {
                _uiState.value = SettingsViewState(profile = it)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setSuccess(isSuccess: Boolean) {
        _uiState.value = _uiState.value.copy(isSuccess = isSuccess)
    }

    fun setError(errorMessage: String?) {
        _uiState.value = _uiState.value.copy(errorMessage = errorMessage)
    }

    fun updateUsername(username: String) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val currentTimestamp = System.currentTimeMillis()
                ownProfileRepository.setUsername(username)
                ownProfileRepository.setUpdateTimestamp(currentTimestamp)
                ownAccountRepository.setProfileUpdateTimestamp(currentTimestamp)
                setSuccess(true)
            } catch (e: Exception) {
                setError("Failed to update username")
            } finally {
                setLoading(false)
            }
        }
    }

    fun updateProfileImage(profileImageUri: Uri) {
        viewModelScope.launch {
            setLoading(true)
            try {
                fileManager.saveProfileImage(
                    profileImageUri,
                    ownAccountRepository.getAccount().accountId
                )?.let { profileImageName ->
                    val currentTimestamp = System.currentTimeMillis()
                    ownProfileRepository.setImageFileName(profileImageName)
                    ownProfileRepository.setUpdateTimestamp(currentTimestamp)
                    ownAccountRepository.setProfileUpdateTimestamp(currentTimestamp)
                    setSuccess(true)
                }
            } catch (e: Exception) {
                setError("Failed to update profile image")
            } finally {
                setLoading(false)
            }
        }
    }
}
