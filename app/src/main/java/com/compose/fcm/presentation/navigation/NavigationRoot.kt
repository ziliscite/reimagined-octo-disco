package com.compose.fcm.presentation.navigation

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.fcm.presentation.auth.AuthAction
import com.compose.fcm.presentation.auth.AuthScreen
import com.compose.fcm.presentation.auth.LoginScreen
import com.compose.fcm.presentation.auth.AuthViewModel
import com.compose.fcm.presentation.chat.ChatViewModel
import com.compose.fcm.presentation.chat.SummaryScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object LoggedInRoute

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    activity: Activity
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            val viewModel = viewModel<AuthViewModel>()
            val state by viewModel.state.collectAsState()

            if (viewModel.sideEffect != null) {
                Toast.makeText(LocalContext.current, viewModel.sideEffect, Toast.LENGTH_SHORT).show()
                viewModel.onAction(AuthAction.RemoveSideEffect)
            }

            LaunchedEffect(Unit) {
                Firebase.auth.currentUser?.let { navigateToLogin(navController) }
            }

            AuthScreen(
                state = state,
                onAction = viewModel::onAction,
                onLoggedIn = {
                    navigateToLogin(navController)
                }
            )
        }
        composable<LoggedInRoute> {
            val viewModel = viewModel<ChatViewModel>()
            val state by viewModel.state.collectAsState()

            DisposableEffect(Unit) {
                val notificationBody = activity.intent.getStringExtra("notification_body")
                viewModel.setSummary(notificationBody)
                onDispose { }
            }

            SummaryScreen(
                state = state,
                onLinkSend = {
                    viewModel.sendLink()
                },
                onLinkChange = {
                    viewModel.onLinkChange(it)
                },
            )
        }
    }
}

fun navigateToLogin(navController: NavHostController) {
    navController.navigate(LoginRoute) {
        // Ensures that the LoginRoute is added to the back stack
        launchSingleTop = true
    }

    // Navigate to LoggedInRoute with popUpTo logic fixed
    navController.navigate(LoggedInRoute) {
        popUpTo(LoginRoute) {
            inclusive = true
        }
    }
}
