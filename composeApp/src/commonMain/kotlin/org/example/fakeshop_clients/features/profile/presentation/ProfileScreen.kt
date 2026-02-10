package org.example.fakeshop_clients.features.profile.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.fakeshop_clients.features.profile.presentation.components.ProfileContent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = koinViewModel(),
    languageSection: @Composable (() -> Unit)? = null
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        }
    ) { paddingValues ->
        ProfileContent(
            profileState = uiState,
            onEvent = profileViewModel::onEvent,
            modifier = Modifier.padding(paddingValues),
            languageSection = languageSection
        )
    }
}