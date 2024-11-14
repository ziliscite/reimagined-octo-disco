package com.compose.fcm.presentation.chat

import com.compose.fcm.data.dto.SummaryResponse
import com.compose.fcm.repository.SummaryResult

data class ChatState(
    val isEnteringToken: Boolean = true,
    val token: String = "",
    val message: String = "",
    val link: String = "",
    val summary: SummaryResult<SummaryResponse> = SummaryResult.Loading,
    val previousSummary: SummaryResponse? = null
)