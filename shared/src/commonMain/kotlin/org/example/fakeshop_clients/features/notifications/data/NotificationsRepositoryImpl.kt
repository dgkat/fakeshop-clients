package org.example.fakeshop_clients.features.notifications.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.notifications.data.models.NotificationPreferencesRequest
import org.example.fakeshop_clients.features.notifications.domain.NotificationsRepository
import org.example.fakeshop_clients.features.notifications.domain.models.NotificationPreferences

class NotificationsRepositoryImpl(
    private val datasource: NotificationsDatasource
) : NotificationsRepository {

    override suspend fun registerDeviceToken(token: String, platform: String): Result<Unit, NetworkError> {
        return datasource.registerDeviceToken(token, platform)
    }

    override suspend fun removeDeviceToken(token: String): Result<Unit, NetworkError> {
        return datasource.removeDeviceToken(token)
    }

    override suspend fun getPreferences(): Result<NotificationPreferences, NetworkError> {
        return datasource.getPreferences().map { response ->
            NotificationPreferences(priceDropEnabled = response.priceDropEnabled)
        }
    }

    override suspend fun updatePreferences(priceDropEnabled: Boolean): Result<NotificationPreferences, NetworkError> {
        return datasource.updatePreferences(NotificationPreferencesRequest(priceDropEnabled)).map { response ->
            NotificationPreferences(priceDropEnabled = response.priceDropEnabled)
        }
    }
}
