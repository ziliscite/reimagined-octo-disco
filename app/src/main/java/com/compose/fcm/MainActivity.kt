package com.compose.fcm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.compose.fcm.data.dto.SummaryResponse
import com.compose.fcm.presentation.chat.ChatViewModel
import com.compose.fcm.presentation.chat.SummaryScreen
import com.compose.fcm.repository.SummaryResult
import com.compose.fcm.ui.theme.PushnotificationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        setContent {
            PushnotificationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { inset ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inset),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LaunchedEffect(Unit) {
                            viewModel.setSummary(intent.getStringExtra("notification_body"))
                        }

                        val state by viewModel.state.collectAsState()

                        SummaryScreen(
                            state,
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
