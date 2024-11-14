package com.compose.fcm.data.remote

import com.compose.fcm.data.dto.SendLink
import com.compose.fcm.data.dto.SummaryResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface YTSumApi {
    @POST("/summarize/mobile")
    suspend fun sendLink(
        @Body body: SendLink
    ): SummaryResponse
}
