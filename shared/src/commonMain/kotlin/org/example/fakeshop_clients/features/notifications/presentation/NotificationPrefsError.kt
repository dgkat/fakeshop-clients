package org.example.fakeshop_clients.features.notifications.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface NotificationPrefsError {
    data class Network(val error: NetworkError) : NotificationPrefsError
}
