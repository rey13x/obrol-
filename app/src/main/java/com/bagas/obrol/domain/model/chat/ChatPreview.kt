package com.bagas.obrol.domain.model.chat

import com.bagas.obrol.domain.model.device.Contact
import com.bagas.obrol.domain.model.message.Message

data class ChatPreview(
    val contact: Contact,
    val unreadMessagesCount: Int,
    val lastMessage: Message?
) : Comparable<ChatPreview> {
    override fun compareTo(other: ChatPreview): Int {
        return compareValuesBy(this, other, { it.lastMessage }, { it.contact.profile.username }, { it.unreadMessagesCount })
    }
}