package com.compose.fcm.domain.auth

import android.util.Patterns.EMAIL_ADDRESS

fun String.validateEmail(): Boolean {
    return this.isNotEmpty() && EMAIL_ADDRESS.matcher(this).matches()
}

fun String.validatePassword(): Boolean {
    return this.isNotEmpty() && this.length >= 6 && this.length <= 20
}
