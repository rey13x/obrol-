package com.bagas.obrol.ui.screen.settings

import com.bagas.obrol.domain.model.device.Profile

data class SettingsViewState(
    val profile: Profile = Profile(0, 0, "username", null),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)