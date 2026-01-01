package com.bagas.obrol.ui.screen.home

import com.bagas.obrol.domain.model.chat.ChatPreview

data class HomeViewState(
    val chatPreviews: List<ChatPreview> = listOf(),
    val onlineChats: Set<Long> = setOf()
)
