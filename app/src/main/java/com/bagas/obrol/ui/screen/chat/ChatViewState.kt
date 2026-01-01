package com.bagas.obrol.ui.screen.chat

import com.bagas.obrol.domain.model.device.Contact
import com.bagas.obrol.domain.model.message.Message

data class ChatViewState(
    val contact: Contact? = null,
    val messages: List<Message> = emptyList(),
    val isOpponentTyping: Boolean = false,
    val isOpponentOnline: Boolean = false
)
