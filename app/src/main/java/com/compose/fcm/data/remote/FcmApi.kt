package com.compose.fcm.data.remote

import com.compose.fcm.data.dto.SendMessage
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {
    @POST("/send")
    suspend fun sendMessage(
        @Body body: SendMessage
    )

    @POST("/broadcast")
    suspend fun broadcast(
        @Body body: SendMessage
    )
}
