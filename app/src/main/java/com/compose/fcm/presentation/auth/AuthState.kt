package com.compose.fcm.presentation.auth

data class AuthState(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirmedPassword: String? = null,
    val passwordVisibility: Boolean = false,
    val confirmedPasswordVisibility: Boolean = false,
    val isRegister: Boolean = false,
    val isLoading: Boolean = false,
    val loggedIn: Boolean = false
)

