package org.example.fakeshop_clients.features.notifications.presentation

sealed class NotificationPrefsEvent {
    data object LoadPreferences : NotificationPrefsEvent()
    data class TogglePriceDrop(val enabled: Boolean) : NotificationPrefsEvent()
}
