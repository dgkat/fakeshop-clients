package org.example.fakeshop_clients.features.profile.presentation

sealed class ProfileEvent {
    data class EmailChanged(val email: String) : ProfileEvent()
    data class PasswordChanged(val password: String) : ProfileEvent()
    data object LoginClicked : ProfileEvent()
    data object SignUpClicked : ProfileEvent()
    data object LogoutClicked : ProfileEvent()
}