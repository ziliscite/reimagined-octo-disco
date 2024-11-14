package com.compose.fcm.presentation.chat

data class ChatState(
    val isEnteringToken: Boolean = true,
    val token: String = "",
    val message: String = ""
)