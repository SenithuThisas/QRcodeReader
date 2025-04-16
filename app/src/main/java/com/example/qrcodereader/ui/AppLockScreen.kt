package com.example.qrcodereader.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.qrcodereader.R

@Composable
fun AppLockScreen(
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val biometricManager = remember { BiometricManager.from(context) }

    // Check if biometric authentication is available
    val canAuthenticate = remember {
        biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    }

    // UI to show if authentication is successful or not
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_lock),
            contentDescription = "Locked",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "App Locked",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            Button(
                onClick = {
                    authenticateWithBiometrics(context, onSuccess = onUnlock)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = "Authenticate")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unlock with Biometrics")
            }
        } else {
            Text(
                text = "Biometric authentication not available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

fun authenticateWithBiometrics(
    context: Context,
    onSuccess: () -> Unit,
    onError: (String) -> Unit = {}
) {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Authenticate to unlock")
        .setSubtitle("Use your biometric credential to access the app")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()

    val biometricPrompt = BiometricPrompt(
        context as FragmentActivity,
        ContextCompat.getMainExecutor(context),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess() // Unlock action
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString()) // Handle error case
            }
        }
    )

    biometricPrompt.authenticate(promptInfo)
}

@Preview(showBackground = true)
@Composable
fun PreviewAppLockScreen() {
    AppLockScreen(onUnlock = {})
}
