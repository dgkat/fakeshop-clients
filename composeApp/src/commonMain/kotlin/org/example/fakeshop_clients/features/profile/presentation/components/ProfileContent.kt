package org.example.fakeshop_clients.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import fakeshop_clients.composeapp.generated.resources.Res
import fakeshop_clients.composeapp.generated.resources.email
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
    when {
        profileState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        profileState.isLoggedIn -> {
            LoggedInContent(
                isProcessing = profileState.isProcessing,
                error = profileState.error?.let { profileErrorMessage(it) },
                onEvent = onEvent,
                modifier = modifier,
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
                modifier = modifier
            )
        }
    }
}

@Composable
fun LoggedInContent(
    isProcessing: Boolean,
    error: String?,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier,
    notificationPrefsSection: @Composable (() -> Unit)? = null,
    languageSection: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UserCard()

        AccountLinksCard()

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        notificationPrefsSection?.invoke()

        languageSection?.invoke()

        OutlinedButton(
            onClick = { onEvent(ProfileEvent.LogoutClicked) },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.error
            )
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = stringResource(Res.string.logout),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun UserCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "My Account",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Manage your profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AccountLinksCard() {
    data class AccountLink(val icon: ImageVector, val title: String, val subtitle: String)

    val links = listOf(
        AccountLink(Icons.Outlined.ShoppingBag, "My orders", "3 active"),
        AccountLink(Icons.Filled.Favorite, "My list", "12 saved"),
        AccountLink(Icons.Outlined.LocationOn, "Addresses", "2 on file"),
        AccountLink(Icons.Outlined.CreditCard, "Payment methods", "Visa •• 4029"),
        AccountLink(Icons.Outlined.CardGiftcard, "Gift cards & credit", "$0.00"),
        AccountLink(Icons.Outlined.HeadsetMic, "Help & support", "")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            links.forEachIndexed { index, link ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = link.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = link.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (link.subtitle.isNotEmpty()) {
                            Text(
                                text = link.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (index < links.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
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
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(Res.string.welcome),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Sign in to see your orders and saved items.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(28.dp))

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
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))

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
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = { onEvent(ProfileEvent.LoginClicked) },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(Res.string.login),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { onEvent(ProfileEvent.SignUpClicked) },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(
                text = stringResource(Res.string.sign_up),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "OR",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text(
                text = "G",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Continue with Google")
        }

        Spacer(modifier = Modifier.height(8.dp))
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
