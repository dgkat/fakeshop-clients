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
import fakeshop_clients.composeapp.generated.resources.Res
import fakeshop_clients.composeapp.generated.resources.email
import fakeshop_clients.composeapp.generated.resources.logged_in
import fakeshop_clients.composeapp.generated.resources.login
import fakeshop_clients.composeapp.generated.resources.logout
import fakeshop_clients.composeapp.generated.resources.password
import fakeshop_clients.composeapp.generated.resources.profile_error_account_locked
import fakeshop_clients.composeapp.generated.resources.profile_error_account_not_verified
import fakeshop_clients.composeapp.generated.resources.profile_error_email_already_exists
import fakeshop_clients.composeapp.generated.resources.profile_error_email_too_long
import fakeshop_clients.composeapp.generated.resources.profile_error_invalid_credentials
import fakeshop_clients.composeapp.generated.resources.profile_error_invalid_email_format
import fakeshop_clients.composeapp.generated.resources.profile_error_network
import fakeshop_clients.composeapp.generated.resources.profile_error_password_too_long
import fakeshop_clients.composeapp.generated.resources.profile_error_password_too_short
import fakeshop_clients.composeapp.generated.resources.profile_error_rate_limited
import fakeshop_clients.composeapp.generated.resources.profile_error_server
import fakeshop_clients.composeapp.generated.resources.profile_error_unknown
import fakeshop_clients.composeapp.generated.resources.profile_error_weak_password
import fakeshop_clients.composeapp.generated.resources.sign_up
import fakeshop_clients.composeapp.generated.resources.welcome
import org.example.fakeshop_clients.features.profile.presentation.ProfileError
import org.example.fakeshop_clients.features.profile.presentation.ProfileEvent
import org.example.fakeshop_clients.features.profile.presentation.ProfileState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileContent(
    profileState: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier,
    notificationPrefsSection: @Composable (() -> Unit)? = null,
    languageSection: @Composable (() -> Unit)? = null
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
                    error = profileState.error?.let { profileErrorMessage(it) },
                    onEvent = onEvent,
                    notificationPrefsSection = notificationPrefsSection,
                    languageSection = languageSection
                )
            }
            else -> {
                LoggedOutContent(
                    email = profileState.email,
                    password = profileState.password,
                    isProcessing = profileState.isProcessing,
                    error = profileState.error?.let { profileErrorMessage(it) },
                    onEvent = onEvent,
                    languageSection = languageSection
                )
            }
        }
    }
}

@Composable
fun LoggedInContent(
    isProcessing: Boolean,
    error: String?,
    onEvent: (ProfileEvent) -> Unit,
    notificationPrefsSection: @Composable (() -> Unit)? = null,
    languageSection: @Composable (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.logged_in),
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
                Text(stringResource(Res.string.logout))
            }
        }

        notificationPrefsSection?.invoke()

        languageSection?.invoke()
    }
}

@Composable
fun LoggedOutContent(
    email: String,
    password: String,
    isProcessing: Boolean,
    error: String?,
    onEvent: (ProfileEvent) -> Unit,
    languageSection: @Composable (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Text(
            text = stringResource(Res.string.welcome),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = email,
            onValueChange = { onEvent(ProfileEvent.EmailChanged(it)) },
            label = { Text(stringResource(Res.string.email)) },
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
            label = { Text(stringResource(Res.string.password)) },
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
                    Text(stringResource(Res.string.login))
                }
            }

            OutlinedButton(
                onClick = { onEvent(ProfileEvent.SignUpClicked) },
                enabled = !isProcessing,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(Res.string.sign_up))
            }
        }

        languageSection?.invoke()
    }
}

@Composable
private fun profileErrorMessage(error: ProfileError): String = when (error) {
    ProfileError.InvalidCredentials -> stringResource(Res.string.profile_error_invalid_credentials)
    ProfileError.AccountLocked -> stringResource(Res.string.profile_error_account_locked)
    ProfileError.AccountNotVerified -> stringResource(Res.string.profile_error_account_not_verified)
    ProfileError.EmailAlreadyExists -> stringResource(Res.string.profile_error_email_already_exists)
    ProfileError.InvalidEmailFormat -> stringResource(Res.string.profile_error_invalid_email_format)
    ProfileError.EmailTooLong -> stringResource(Res.string.profile_error_email_too_long)
    ProfileError.WeakPassword -> stringResource(Res.string.profile_error_weak_password)
    ProfileError.PasswordTooShort -> stringResource(Res.string.profile_error_password_too_short)
    ProfileError.PasswordTooLong -> stringResource(Res.string.profile_error_password_too_long)
    ProfileError.RateLimited -> stringResource(Res.string.profile_error_rate_limited)
    ProfileError.ServerError -> stringResource(Res.string.profile_error_server)
    is ProfileError.Network -> stringResource(Res.string.profile_error_network)
    ProfileError.Unknown -> stringResource(Res.string.profile_error_unknown)
}