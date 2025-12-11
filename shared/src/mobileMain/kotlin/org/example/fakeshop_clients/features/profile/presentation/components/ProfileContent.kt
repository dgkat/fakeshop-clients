package org.example.fakeshop_clients.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.example.fakeshop_clients.features.profile.presentation.ProfileEvent
import org.example.fakeshop_clients.features.profile.presentation.ProfileState

@Composable
fun ProfileContent(
    profileState: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            profileState.isLoading -> {
                CircularProgressIndicator()
            }
            profileState.isLoggedIn -> {
                LoggedInContent(
                    isProcessing = profileState.isProcessing,
                    error = profileState.error,
                    onEvent = onEvent
                )
            }
            else -> {
                LoggedOutContent(
                    email = profileState.email,
                    password = profileState.password,
                    isProcessing = profileState.isProcessing,
                    error = profileState.error,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
fun LoggedInContent(
    isProcessing: Boolean,
    error: String?,
    onEvent: (ProfileEvent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "You are logged in",
            style = MaterialTheme.typography.headlineSmall
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { onEvent(ProfileEvent.LogoutClicked) },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Logout")
            }
        }
    }
}

@Composable
fun LoggedOutContent(
    email: String,
    password: String,
    isProcessing: Boolean,
    error: String?,
    onEvent: (ProfileEvent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = email,
            onValueChange = { onEvent(ProfileEvent.EmailChanged(it)) },
            label = { Text("Email") },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { onEvent(ProfileEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onEvent(ProfileEvent.LoginClicked) },
                enabled = !isProcessing,
                modifier = Modifier.weight(1f)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Log In")
                }
            }

            OutlinedButton(
                onClick = { onEvent(ProfileEvent.SignUpClicked) },
                enabled = !isProcessing,
                modifier = Modifier.weight(1f)
            ) {
                Text("Sign Up")
            }
        }
    }
}