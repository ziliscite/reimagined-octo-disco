package com.compose.fcm.presentation.auth

import androidx.lifecycle.ViewModel
import com.compose.fcm.domain.auth.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel: ViewModel() {
    var state = MutableStateFlow(AuthState())
        private set

    fun onAction(action: AuthAction) {
        when(action) {
            is AuthAction.OnEmailChange -> {
                state.value = state.value.copy(email = action.email)
            }
            is AuthAction.OnPasswordChange -> {
                state.value = state.value.copy(password = action.password)
            }

            is AuthAction.OnEmailSignIn -> {
                when (action.response) {
                    is AuthResponse.Success -> {
                        state.value = state.value.copy(error = null)
                        state.value = state.value.copy(isLoading = false)
                        state.value = state.value.copy(loggedIn = true)
                    }
                    is AuthResponse.Error -> {
                        state.value = state.value.copy(isLoading = false)
                        state.value = state.value.copy(error = action.response.message)
                    }
                    is AuthResponse.Loading -> {
                        state.value = state.value.copy(error = null)
                        state.value = state.value.copy(isLoading = true)
                    }
                }
            }
            is AuthAction.OnEmailSignUp -> {
                when (action.response) {
                    is AuthResponse.Success -> {
                        state.value = state.value.copy(error = null)
                        state.value = state.value.copy(isLoading = false)
                        state.value = state.value.copy(loggedIn = true)
                    }
                    is AuthResponse.Error -> {
                        state.value = state.value.copy(isLoading = false)
                        state.value = state.value.copy(error = action.response.message)
                    }
                    is AuthResponse.Loading -> {
                        state.value = state.value.copy(error = null)
                        state.value = state.value.copy(isLoading = true)
                    }
                }
            }
            is AuthAction.OnGoogleSignIn -> {
                when (action.response) {
                    is AuthResponse.Success -> {
                        state.value = state.value.copy(error = null)
                        state.value = state.value.copy(isLoading = false)
                        state.value = state.value.copy(loggedIn = true)
                    }
                    is AuthResponse.Error -> {
                        state.value = state.value.copy(isLoading = false)
                        state.value = state.value.copy(error = action.response.message)
                    }
                    is AuthResponse.Loading -> {
                        state.value = state.value.copy(error = null)
                        state.value = state.value.copy(isLoading = true)
                    }
                }
            }

            is AuthAction.OnPasswordVisibilityChange -> {
               state.value = state.value.copy(passwordVisibility = !state.value.passwordVisibility)
            }

            is AuthAction.OnSubmit -> {
                validateCredentials(state.value.email, state.value.password).let { isValid ->
                    if (isValid) {
                        state.value = state.value.copy(error = null)
                    } else {
                        state.value = state.value.copy(error = "Invalid credentials")
                    }
                }
            }
        }
    }

    private fun clearSideEffects() {
        state.value = state.value.copy(error = null)
        state.value = state.value.copy(isLoading = false)
    }

    private fun validateEmail(email: String?): Boolean {
        if (email == null) return false
        return email.isNotEmpty() // && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String?): Boolean {
        if (password == null) return false
        return password.isNotEmpty() // && password.length >= 6 && password.length <= 20
    }

    private fun validateCredentials(email: String?, password: String?): Boolean {
        return validateEmail(email) && validatePassword(password)
    }
}
