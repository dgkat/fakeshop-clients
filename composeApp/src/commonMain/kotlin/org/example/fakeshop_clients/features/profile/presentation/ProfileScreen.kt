package org.example.fakeshop_clients.features.profile.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsEvent
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewModel
import org.example.fakeshop_clients.features.profile.presentation.components.NotificationPrefsSection
import org.example.fakeshop_clients.features.profile.presentation.components.ProfileContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = koinViewModel(),
    notificationPrefsViewModel: NotificationPrefsViewModel = koinViewModel(),
    languageSection: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues()
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val notificationPrefsState by notificationPrefsViewModel.state.collectAsStateWithLifecycle()

    ProfileContent(
        profileState = uiState,
        onEvent = profileViewModel::onEvent,
        modifier = Modifier.padding(contentPadding),
        notificationPrefsSection = {
            NotificationPrefsSection(
                state = notificationPrefsState,
                onTogglePriceDrop = { enabled ->
                    notificationPrefsViewModel.onEvent(
                        NotificationPrefsEvent.TogglePriceDrop(enabled)
                    )
                }
            )
        },
        languageSection = languageSection
    )
}
