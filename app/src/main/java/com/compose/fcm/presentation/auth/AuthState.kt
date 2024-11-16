package com.compose.fcm.presentation.auth

data class AuthState(
    val email: String? = null,
    val password: String? = null,
    val confirmedPassword: String? = null,
    val passwordVisibility: Boolean = false,
    val isRegister: Boolean = false,
    val isLoading: Boolean = false,
    val loggedIn: Boolean = false
)

