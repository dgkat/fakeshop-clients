package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse
import org.example.fakeshop_clients.core.auth.data.models.WebLogoutRequest
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.core.network.UrlProvider

class WebLogoutUser(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : LogoutUser {
    override suspend fun invoke(deviceToken: String?): Result<Unit, NetworkError> {
        return authClient.post<LogoutResponse, WebLogoutRequest>(
            path = "${baseUrl()}/auth/logout",
            body = WebLogoutRequest(deviceToken)
        ).map { _ ->
            Unit
        }
    }
}