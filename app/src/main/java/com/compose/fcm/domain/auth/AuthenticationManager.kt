package com.compose.fcm.domain.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.compose.fcm.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AuthenticationManager(
    private val context: Context
) {
    private val auth = Firebase.auth
    private val credManager = CredentialManager.create(context)

    // Use callback to return values from the listener
    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = flow {
        emit(AuthResponse.Loading)
        try {
            Log.d("AuthManager", "Creating credential for email: $email")
            credManager.createCredential(context, CreatePasswordRequest(email, password))

            Log.d("AuthManager", "Firebase creating user with email: $email")
            auth.createUserWithEmailAndPassword(email, password).await()
            Log.d("AuthManager", "Account creation successful")
            emit(AuthResponse.Success)
        } catch (e: CreateCredentialCancellationException) {
            Log.e("AuthManager", "Credential creation cancelled: ${e.message}")
            emit(AuthResponse.Error("Cancelled"))
        } catch (e: CreateCredentialException) {
            Log.e("AuthManager", "Credential creation failed: ${e.message}")
            emit(AuthResponse.Error("Failed"))
        }
    }

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> = flow {
        emit(AuthResponse.Loading)

        // Attempt to retrieve credentials but do not interrupt the flow if it fails
        try {
            Log.d("AuthManager", "Attempting to retrieve credential for email: $email")
            val credentialResponse = credManager.getCredential(
                context, GetCredentialRequest(credentialOptions = listOf(GetPasswordOption()))
            )
            val credential = credentialResponse.credential as? PasswordCredential
            Log.d("AuthManager", "Credential retrieved. ID: ${credential?.id}")
        } catch (e: GetCredentialException) {
            // Log the issue and proceed without emitting an error to continue the login process
            Log.w("AuthManager", "Credential retrieval failed: ${e.message}. Proceeding with email/password login.")
        } catch (e: NoCredentialException) {
            credManager.createCredential(context, CreatePasswordRequest(email, password))
            Log.w("AuthManager", "No credential found. Proceeding with email/password login.")
        }

        // Proceed with the normal email/password sign-in regardless of the credential manager's result
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d("AuthManager", "Sign-in successful")
            emit(AuthResponse.Success)
        } catch (e: Exception) {
            // Catch any other exceptions during the sign-in process
            Log.e("AuthManager", "Sign-in failed: ${e.message}")
            emit(AuthResponse.Error("Sign-in failed: ${e.message}"))
        }
    }

    private fun createNonce(): String {
        try {
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it ->
                str + "%02x".format(it)
            }
            Log.d("AuthManager", "Nonce created: $hashedNonce")
            return hashedNonce
        } catch (e: Exception) {
            Log.e("AuthManager", "Error creating nonce: ${e.message}")
            throw e
        }
    }

    fun signInWithGoogle(): Flow<AuthResponse> = flow {
        emit(AuthResponse.Loading)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            Log.d("AuthManager", "Attempting to get Google credential")
            val credential = credManager.getCredential(context, request).credential
            Log.d("AuthManager", "Google credential received. Type: ${credential.type}")

            if (credential !is CustomCredential) {
                Log.e("AuthManager", "Credential not found")
                emit(AuthResponse.Error("Credential not found"))
            }

            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                Log.e("AuthManager", "Invalid credential type")
                emit(AuthResponse.Error("Invalid credential type"))
            }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Log.d("AuthManager", "Google ID Token: ${googleIdTokenCredential.idToken}")

            val firebaseCredential = GoogleAuthProvider.getCredential(
                googleIdTokenCredential.idToken, null
            )
            Log.d("AuthManager", "Signing in with Google credential")
            auth.signInWithCredential(firebaseCredential).await()
            Log.d("AuthManager", "Google sign-in successful")
            emit(AuthResponse.Success)
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("AuthManager", "Google ID token parsing failed: ${e.message}")
            emit(AuthResponse.Error(e.message ?: ""))
        } catch (e: Exception) {
            Log.e("AuthManager", "Google sign-in failed: ${e.message}")
            emit(AuthResponse.Error(e.message ?: ""))
        }
    }
}

sealed interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val message: String) : AuthResponse
    data object Loading : AuthResponse
}
