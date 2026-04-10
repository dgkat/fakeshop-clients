package org.example.fakeshop_clients.features.notifications.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.deleteNoContent
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.data.postNoContent
import org.example.fakeshop_clients.core.data.putNoContent
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.notifications.data.models.DeviceTokenRequest
import org.example.fakeshop_clients.features.notifications.data.models.NotificationPreferencesRequest
import org.example.fakeshop_clients.features.notifications.data.models.NotificationPreferencesResponse

class NotificationsDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : NotificationsDatasource {

    override suspend fun registerDeviceToken(token: String, platform: String): Result<Unit, NetworkError> {
        return authClient.postNoContent("${baseUrl()}/device-tokens", DeviceTokenRequest(token, platform))
    }

    override suspend fun removeDeviceToken(token: String): Result<Unit, NetworkError> {
        return authClient.deleteNoContent("${baseUrl()}/device-tokens/$token")
    }

    override suspend fun getPreferences(): Result<NotificationPreferencesResponse, NetworkError> {
        return authClient.get("${baseUrl()}/notification-preferences")
    }

    override suspend fun updatePreferences(request: NotificationPreferencesRequest): Result<Unit, NetworkError> {
        return authClient.putNoContent("${baseUrl()}/notification-preferences", request)
    }
}
