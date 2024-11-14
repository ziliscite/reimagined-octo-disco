package com.compose.fcm.presentation.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.fcm.data.dto.NotificationBody
import com.compose.fcm.data.dto.SendMessage
import com.compose.fcm.data.remote.FcmApi
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class ChatViewModel: ViewModel() {
    var state by mutableStateOf(ChatState())
        private set

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()


    private val api: FcmApi = Retrofit.Builder()
        .client(client)
        .baseUrl("http://192.168.244.33:8085/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create()

    init { viewModelScope.launch {
        // Subscribe to topic
        // This is to receive messages from broadcast on firebase
        //
        // Firebase send notification to a topic, if we are subscribed, it will receive it
        Firebase.messaging.subscribeToTopic("broadcast").await()
    }}

    // Read token of other device
    fun onRemoteTokenChange(newToken: String) {
        state = state.copy(token = newToken)
    }

    fun onSubmitRemoteToken() {
        state = state.copy(isEnteringToken = false)
    }

    fun onMessageChange(message: String) {
        state = state.copy(message = message)
    }

    fun sendMessage(isBroadcast: Boolean) { viewModelScope.launch {
        val message = SendMessage(
            to = if(isBroadcast) null else state.token,
            notification = NotificationBody(
                title = "New Message",
                body = state.message
            )
        )

        try {
            if (isBroadcast) {
                api.broadcast(message)
            } else {
                api.sendMessage(message)
            }

            state = state.copy(message = "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }}
}
