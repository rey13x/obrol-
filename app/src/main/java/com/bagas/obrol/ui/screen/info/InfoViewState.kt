package com.bagas.obrol.ui.screen.info

import com.bagas.obrol.domain.model.device.Profile
import com.bagas.obrol.domain.model.message.FileMessage
import com.bagas.obrol.domain.model.message.TextMessage

data class InfoViewState(
    val profile: Profile = Profile(0, 0, "username", null),
    val imageMessages: List<FileMessage> = listOf(),
    val linkMessages: List<TextMessage> = listOf(),
    val fileMessages: List<FileMessage> = listOf()
)
