package com.compose.fcm.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import  com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {
    // Authenticate with refresh token
    // To send push notification upon a longer period of time to a specific user
    // We need the reference to the current token of the user
    // When get token from firebase through auth, we take the token and push it to the server
    // And save it in the database for that user
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Update server
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Could respond to received messages
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}
