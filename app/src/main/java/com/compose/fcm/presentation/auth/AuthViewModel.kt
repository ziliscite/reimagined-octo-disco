package com.compose.fcm.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.compose.fcm.domain.auth.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel: ViewModel() {
    var state = MutableStateFlow(AuthState())
        private set

    var sideEffect by mutableStateOf<String?>(null)
        private set

    fun onAction(action: AuthAction) {
        when(action) {
            is AuthAction.OnAuth -> {
                when (action.response) {
                    is AuthResponse.Success -> {
                        state.value = state.value.copy(isLoading = false)
                        state.value = state.value.copy(loggedIn = true)
                    }
                    is AuthResponse.Error -> {
                        state.value = state.value.copy(isLoading = false)
                        sideEffect = action.response.message
                    }
                    is AuthResponse.Loading -> {
                        state.value = state.value.copy(isLoading = true)
                    }
                }
            }

            is AuthAction.OnUsernameChange -> {
                state.value = state.value.copy(username = action.username)
            }
            is AuthAction.OnEmailChange -> {
                state.value = state.value.copy(email = action.email)
            }
            is AuthAction.OnPasswordChange -> {
                state.value = state.value.copy(password = action.password)
            }
            is AuthAction.OnConfirmedPasswordChange -> {
                state.value = state.value.copy(confirmedPassword = action.password)
            }
            is AuthAction.OnPasswordVisibilityChange -> {
               state.value = state.value.copy(passwordVisibility = !state.value.passwordVisibility)
            }
            is AuthAction.OnConfirmedPasswordVisibilityChange -> {
                state.value = state.value.copy(confirmedPasswordVisibility = !state.value.confirmedPasswordVisibility)
            }

            is AuthAction.SideEffect -> {
                sideEffect = action.message
            }
            is AuthAction.RemoveSideEffect -> {
                sideEffect = null
            }

            is AuthAction.OnToggleIsRegister -> {
                state.value = state.value.copy(
                    isRegister = !state.value.isRegister,
                    email = "",
                    password = "",
                    confirmedPassword = ""
                )
            }
        }
    }
}
