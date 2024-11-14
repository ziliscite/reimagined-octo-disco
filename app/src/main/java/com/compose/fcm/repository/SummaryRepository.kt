package com.compose.fcm.repository

import android.util.Log
import com.compose.fcm.data.dto.SendLink
import com.compose.fcm.data.dto.SummaryResponse
import com.compose.fcm.data.remote.YTSumApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

sealed class SummaryResult<out R> private constructor() {
    data class Success<out T>(val data: T) : SummaryResult<T>()
    data class Failed(val error: String) : SummaryResult<Nothing>()
    data object Loading : SummaryResult<Nothing>()
}

class SummaryRepository{
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val youtubeApi: YTSumApi = Retrofit.Builder()
        .baseUrl("http://192.168.244.33:8085/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create()

    fun sendLink(to: String?, link: String): Flow<SummaryResult<SummaryResponse>> = flow {
        val sendLink = SendLink(to, link)
        emit(SummaryResult.Loading)
        try {
            val response = youtubeApi.sendLink(sendLink)
            emit(SummaryResult.Success(response))
        } catch (e: Exception) {
            emit(SummaryResult.Failed(e.message.toString()))
        }
    }
}