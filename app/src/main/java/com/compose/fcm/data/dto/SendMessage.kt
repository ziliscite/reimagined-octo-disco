package com.compose.fcm.data.dto

data class SendMessage(
    val to: String?, // nullable token
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)
