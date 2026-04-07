package org.example.fakeshop_clients.features.notifications.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.notifications.domain.models.NotificationPreferences

interface NotificationsService {
    suspend fun registerDeviceToken(token: String, platform: String): Result<Unit, NetworkError>
    suspend fun removeDeviceToken(token: String): Result<Unit, NetworkError>
    suspend fun getPreferences(): Result<NotificationPreferences, NetworkError>
    suspend fun togglePriceDrop(enabled: Boolean): Result<NotificationPreferences, NetworkError>
}
