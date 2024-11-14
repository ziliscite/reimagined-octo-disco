package com.compose.fcm.presentation.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.fcm.data.dto.SummaryResponse
import com.compose.fcm.repository.SummaryRepository
import com.compose.fcm.repository.SummaryResult
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel: ViewModel() {
    var state by mutableStateOf(ChatState())
        private set

    init { viewModelScope.launch {
        // Read token of current device
        state = state.copy(token = Firebase.messaging.token.await())
    }}

    private val summaryRepository = SummaryRepository()

    fun onLinkChange(link: String) {
        state = state.copy(link = link)
    }

    fun sendLink() {
        if (state.link.isNotEmpty()) {
            getSummary().onEach { result ->
                state = state.copy(summary = result, link = "")

                when (val previousSummary = state.summary) {
                    is SummaryResult.Success -> {
                        state = state.copy(previousSummary = previousSummary.data)
                    }
                    is SummaryResult.Failed -> { }
                    SummaryResult.Loading -> { }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getSummary(): Flow<SummaryResult<SummaryResponse>> {
        return summaryRepository.sendLink(state.token, state.link)
    }
}
