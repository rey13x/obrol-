package com.bagas.obrol.domain.repository

import com.bagas.obrol.data.local.message.MessageEntity
import com.bagas.obrol.domain.model.chat.ChatPreview
import com.bagas.obrol.domain.model.message.Message
import com.bagas.obrol.domain.model.message.MessageState
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllChatPreviewsAsFlow(): Flow<List<ChatPreview>>

    fun getAllMessagesByAccountIdAsFlow(accountId: Long): Flow<List<Message>>

    fun getAllMediaMessagesByAccountIdAsFlow(accountId: Long): Flow<List<Message>>

    fun getAllMessagesByReceiverAccountId(accountId: Long): List<Message>

    suspend fun getAllMessages(): List<MessageEntity>

    suspend fun getMessageByMessageId(messageId: Long): Message?

    suspend fun addMessage(message: Message): Long

    suspend fun updateMessage(message: Message)
    
    suspend fun updateMessageState(messageId: Long, messageState: MessageState)
}