package com.compose.fcm.presentation.auth

import com.compose.fcm.domain.auth.AuthResponse

sealed interface AuthAction {
    data class OnAuth(val response: AuthResponse): AuthAction
    data object OnToggleIsRegister: AuthAction

    data class OnEmailChange(val email: String): AuthAction
    data class OnPasswordChange(val password: String): AuthAction
    data class OnConfirmedPasswordChange(val password: String): AuthAction
    data object OnPasswordVisibilityChange: AuthAction

    data class SideEffect(val message: String): AuthAction
    data object RemoveSideEffect: AuthAction
}
