package com.compose.fcm.presentation.auth

data class AuthState(
    val email: String? = null,
    val password: String? = null,
    val passwordVisibility: Boolean = false,
    val sideEffect: String? = null,
    val isLoading: Boolean = false,
    val loggedIn: Boolean = false
)
