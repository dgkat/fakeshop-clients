package org.example.fakeshop_clients.features.notifications.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.notifications.data.models.NotificationPreferencesRequest
import org.example.fakeshop_clients.features.notifications.data.models.NotificationPreferencesResponse

interface NotificationsDatasource {
    suspend fun registerDeviceToken(token: String, platform: String): Result<Unit, NetworkError>
    suspend fun removeDeviceToken(token: String): Result<Unit, NetworkError>
    suspend fun getPreferences(): Result<NotificationPreferencesResponse, NetworkError>
    suspend fun updatePreferences(request: NotificationPreferencesRequest): Result<Unit, NetworkError>
}
