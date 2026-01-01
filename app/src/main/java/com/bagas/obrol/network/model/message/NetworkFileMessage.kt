package com.bagas.obrol.network.model.message

import com.bagas.obrol.domain.model.message.FileMessage
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
class NetworkFileMessage(
    val messageId: Long,
    val senderId: Long,
    val receiverId: Long,
    val timestamp: Long,
    val fileName: String,
    val fileBase64: String
) {
    constructor(fileMessage: FileMessage, fileBase64: String)
        : this(fileMessage.messageId, fileMessage.senderId, fileMessage.receiverId, fileMessage.timestamp, fileMessage.fileName, fileBase64)
}