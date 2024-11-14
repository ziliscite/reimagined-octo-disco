package com.compose.fcm.data.dto

data class SendLink(
    val to: String?,
    val link: String
)

data class SummaryResponse(
    val response: String
)