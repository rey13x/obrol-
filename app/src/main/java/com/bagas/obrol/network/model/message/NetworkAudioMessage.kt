package com.bagas.obrol.network.model.message

import com.bagas.obrol.domain.model.message.AudioMessage
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
class NetworkAudioMessage(
    val messageId: Long,
    val senderId: Long,
    val receiverId: Long,
    val timestamp: Long,
    val audioBase64: String
) {
    constructor(audioMessage: AudioMessage, audioBase64: String)
            : this(audioMessage.messageId, audioMessage.senderId, audioMessage.receiverId, audioMessage.timestamp, audioBase64)
}