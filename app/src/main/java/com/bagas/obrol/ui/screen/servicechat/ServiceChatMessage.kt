package com.bagas.obrol.ui.screen.servicechat

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

sealed class ServiceChatMessage {
    data class UserMessage(val text: String) : ServiceChatMessage()

    data class BotMessage(
        val text: String,
        val isFirstMessage: Boolean = false
    ) : ServiceChatMessage()

    data class BotImageMessage(
        @DrawableRes val imageResId: Int,
        val caption: String? = null
    ) : ServiceChatMessage()

    data class BotVideoMessage(
        @RawRes val videoResId: Int
    ) : ServiceChatMessage()

    data class BotFileMessage(
        val fileName: String,
        @RawRes val fileResId: Int
    ) : ServiceChatMessage()

    data class QuestionTemplates(
        val title: String,
        val questions: List<String>
    ) : ServiceChatMessage()

    data class ActionButtons(
        val actions: List<String>
    ) : ServiceChatMessage()
}
