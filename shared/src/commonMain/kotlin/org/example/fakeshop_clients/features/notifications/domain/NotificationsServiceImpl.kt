package org.example.fakeshop_clients.features.notifications.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.notifications.domain.models.NotificationPreferences

class NotificationsServiceImpl(
    private val repository: NotificationsRepository
) : NotificationsService {

    override suspend fun registerDeviceToken(token: String, platform: String): Result<Unit, NetworkError> {
        return repository.registerDeviceToken(token, platform)
    }

    override suspend fun removeDeviceToken(token: String): Result<Unit, NetworkError> {
        return repository.removeDeviceToken(token)
    }

    override suspend fun getPreferences(): Result<NotificationPreferences, NetworkError> {
        return repository.getPreferences()
    }

    override suspend fun togglePriceDrop(enabled: Boolean): Result<NotificationPreferences, NetworkError> {
        return repository.updatePreferences(priceDropEnabled = enabled)
    }
}
