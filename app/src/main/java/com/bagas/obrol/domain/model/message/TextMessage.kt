package com.bagas.obrol.domain.model.message

import com.bagas.obrol.data.local.message.MessageEntity
import com.bagas.obrol.network.model.message.NetworkTextMessage

data class TextMessage(
    override val messageId: Long,
    override val senderId: Long,
    override val receiverId: Long,
    override val timestamp: Long,
    override val messageState: MessageState,
    val text: String
) : Message() {
    constructor(networkTextMessage: NetworkTextMessage, messageState: MessageState) : this(
        messageId = networkTextMessage.messageId,
        senderId = networkTextMessage.senderId,
        receiverId = networkTextMessage.receiverId,
        timestamp = networkTextMessage.timestamp,
        messageState = messageState,
        text = networkTextMessage.text
    )

    override fun toMessageEntity(): MessageEntity {
        return MessageEntity(
            senderId = senderId,
            receiverId = receiverId,
            timestamp = timestamp,
            messageState = messageState,
            messageType = MessageType.TEXT_MESSAGE,
            content = text
        )
    }
}

fun MessageEntity.toTextMessage(): TextMessage {
    return TextMessage(
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        timestamp = timestamp,
        messageState = messageState,
        text = content
    )
}

fun TextMessage.toNetworkTextMessage(): NetworkTextMessage {
    return NetworkTextMessage(
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        timestamp = timestamp,
        text = text
    )
}