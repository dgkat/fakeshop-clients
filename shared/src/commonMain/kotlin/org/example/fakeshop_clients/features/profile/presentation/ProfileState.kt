package org.example.fakeshop_clients.features.profile.presentation

data class ProfileState(
    val isLoggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val showNotificationsSection: Boolean = false,
    val isLoading: Boolean = true,
    val email: String = "",
    val password: String = "",
    val error: ProfileError? = null,
    val isProcessing: Boolean = false
)