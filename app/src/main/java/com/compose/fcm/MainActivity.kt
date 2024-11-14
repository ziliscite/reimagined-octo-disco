package com.compose.fcm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.compose.fcm.presentation.chat.ChatScreen
import com.compose.fcm.presentation.chat.ChatViewModel
import com.compose.fcm.presentation.chat.EnterTokenDialog
import com.compose.fcm.ui.theme.PushnotificationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            PushnotificationTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val state = viewModel.state
                    if (state.isEnteringToken) {
                        EnterTokenDialog(
                            state.token,
                            viewModel::onRemoteTokenChange,
                            viewModel::onSubmitRemoteToken
                        )
                    } else {
                        ChatScreen(
                            messageText = state.message,
                            onMessageSend = {
                                viewModel.sendMessage(isBroadcast = false)
                            },
                            onMessageBroadcast = {
                                viewModel.sendMessage(isBroadcast = true)
                            },
                            onMessageChange = {
                                viewModel.onMessageChange(it)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}
