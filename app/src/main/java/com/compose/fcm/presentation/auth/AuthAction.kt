package com.compose.fcm.presentation.auth

import com.compose.fcm.domain.auth.AuthResponse

sealed interface AuthAction {
    data class OnEmailSignUp(val response: AuthResponse): AuthAction
    data class OnEmailSignIn(val response: AuthResponse): AuthAction
    data class OnGoogleSignIn(val response: AuthResponse): AuthAction

    data class OnEmailChange(val email: String): AuthAction
    data class OnPasswordChange(val password: String): AuthAction
    data object OnPasswordVisibilityChange: AuthAction

    data class OnSubmit(val email: String, val password: String): AuthAction
}
