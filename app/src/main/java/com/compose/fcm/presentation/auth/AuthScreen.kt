package com.compose.fcm.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.compose.fcm.R
import com.compose.fcm.domain.auth.AuthenticationManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LinearProgressIndicator
import com.compose.fcm.domain.auth.validateEmail
import com.compose.fcm.domain.auth.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    onLoggedIn: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember {
        AuthenticationManager(context)
    }

    if (state.loggedIn) {
        onLoggedIn()
    }

    AnimatedVisibility(visible = state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (state.isRegister) {
        RegisterScreen(
            modifier = modifier,
            state = state,
            onAction = onAction,
            authManager = authManager,
            scope = scope
        )
    } else {
        LoginScreen(
            modifier = modifier,
            state = state,
            onAction = onAction,
            authManager = authManager,
            scope = scope
        )
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    authManager: AuthenticationManager,
    scope: CoroutineScope
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo_2),
            contentDescription = "Logo",
            modifier = Modifier.size(220.dp)
        )

        Text(
            text = "Create your account",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Username Input
        OutlinedTextField(
            value = state.username ?: "",
            onValueChange = {
                onAction(
                    AuthAction.OnUsernameChange(it)
                )
            },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Input
        OutlinedTextField(
            value = state.email ?: "",
            onValueChange = {
                onAction(
                    AuthAction.OnEmailChange(it)
                )
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = state.password ?: "",
            onValueChange = {
                onAction(
                    AuthAction.OnPasswordChange(it)
                )
            },
            label = { Text("Password") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    onAction(AuthAction.OnPasswordVisibilityChange)
                }) {
                    Icon(
                        painter = if (state.passwordVisibility) painterResource(id = R.drawable.baseline_visibility_24) else painterResource(
                            id = R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            visualTransformation = if (state.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = state.confirmedPassword ?: "",
            onValueChange = {
                onAction(
                    AuthAction.OnConfirmedPasswordChange(it)
                )
            },
            label = { Text("Confirm Password") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    onAction(AuthAction.OnConfirmedPasswordVisibilityChange)
                }) {
                    Icon(
                        painter = if (state.confirmedPasswordVisibility) painterResource(id = R.drawable.baseline_visibility_24) else painterResource(
                            id = R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            visualTransformation = if (state.confirmedPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (state.username.isNullOrBlank()) {
                    AuthAction.SideEffect("Please enter your username")
                    return@Button
                }

                if (state.email.isNullOrBlank()) {
                    AuthAction.SideEffect("Please enter your email")
                    return@Button
                }

                if (state.password.isNullOrBlank()) {
                    AuthAction.SideEffect("Please enter your password")
                    return@Button
                }

                if (state.confirmedPassword.isNullOrBlank()) {
                    AuthAction.SideEffect("Please confirm your password")
                    return@Button
                }

                if (state.password != state.confirmedPassword) {
                    AuthAction.SideEffect("Passwords do not match")
                    return@Button
                }

                if (!(state.email.validateEmail() && state.password.validatePassword())) {
                    AuthAction.SideEffect("Please enter valid email and password")
                    return@Button
                }

                authManager.createAccountWithEmail(state.username, state.email, state.password).onEach {
                    onAction(AuthAction.OnAuth(it))
                }.launchIn(scope)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Sign Up", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Sign in",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp).clickable {
                onAction(AuthAction.OnToggleIsRegister)
            }
        )
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    authManager: AuthenticationManager,
    scope: CoroutineScope
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo_2),
            contentDescription = "Logo",
            modifier = Modifier.size(220.dp)
        )

        Text(
            text = "Sign in to your account",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email Input
        OutlinedTextField(
            value = state.email ?: "",
            onValueChange = {
                onAction(
                    AuthAction.OnEmailChange(it)
                )
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = state.password ?: "",
            onValueChange = {
                onAction(
                    AuthAction.OnPasswordChange(it)
                )
            },
            label = { Text("Password") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    onAction(AuthAction.OnPasswordVisibilityChange)
                }) {
                    Icon(
                        painter = if (state.passwordVisibility) painterResource(id = R.drawable.baseline_visibility_24) else painterResource(
                            id = R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            visualTransformation = if (state.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Forgot password?",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Log In Button
        Button(
            onClick = {
                if (state.email.isNullOrBlank() || state.password.isNullOrBlank()) {
                    AuthAction.SideEffect("Please enter email and password")
                    return@Button
                }

                if (!(state.email.validateEmail() && state.password.validatePassword())) {
                    AuthAction.SideEffect("Please enter valid email and password")
                    return@Button
                }

                authManager.loginWithEmail(state.email, state.password).onEach {
                    onAction(AuthAction.OnAuth(it))
                }.launchIn(scope)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Sign In", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Don't have an account? Sign up",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp).clickable {
                onAction(AuthAction.OnToggleIsRegister)
            }
        )

        DividerWithText("Sign In with")

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In Button
        OutlinedButton(
            onClick = {
                authManager.signInWithGoogle().onEach {
                    onAction(AuthAction.OnAuth(it))
                }.launchIn(scope)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.google_logo), // Replace with your Google icon resource
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Google")
            }
        }
    }
}

@Composable
fun DividerWithText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}
