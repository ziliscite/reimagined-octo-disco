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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel: ViewModel() {
    private val _state = MutableStateFlow(ChatState(summary = SummaryResult.Loading))
    val state: StateFlow<ChatState> get() = _state

    init {
        viewModelScope.launch {
            _state.value = state.value.copy(token = Firebase.messaging.token.await())
        }
    }

    private val summaryRepository = SummaryRepository()

    fun onLinkChange(link: String) {
        _state.value = state.value.copy(link = link)
    }

    fun sendLink() {
        if (state.value.link.isNotEmpty()) {
            getSummary().onEach { result ->
                _state.value = state.value.copy(summary = result, link = "")
            }.launchIn(viewModelScope)
        }
    }

    private fun getSummary(): Flow<SummaryResult<SummaryResponse>> {
        return summaryRepository.sendLink(state.value.token, state.value.link)
    }

    fun setSummary(notificationBody: String?) { notificationBody?.let {
        _state.value = state.value.copy(summary = SummaryResult.Success(SummaryResponse(it)))
    }}
}
