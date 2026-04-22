package org.example.fakeshop_clients.features.notifications.presentation

data class NotificationPrefsState(
    val priceDropEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val isToggling: Boolean = false,
    val error: NotificationPrefsError? = null,
    val writesBlocked: Boolean = false
)
