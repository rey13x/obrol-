package com.bagas.obrol.data.local.message

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bagas.obrol.domain.model.message.MessageState
import com.bagas.obrol.domain.model.message.MessageType
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
@Entity
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val messageId: Long = 0,
    val senderId: Long,
    val receiverId: Long,
    val timestamp: Long,
    val messageState: MessageState,
    val messageType: MessageType,
    val content: String
)