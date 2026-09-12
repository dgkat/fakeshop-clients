package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.core.interactions.domain.SessionIdProvider
import org.example.fakeshop_clients.core.network.UrlProvider

class WebLogoutUser(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider,
    private val sessionIdProvider: SessionIdProvider
) : LogoutUser {
    override suspend fun invoke(): Result<Unit, NetworkError> {
        return authClient.post<LogoutResponse, Unit>(
            path = "${baseUrl()}/auth/logout",
            body = Unit
        ).map { _ ->
            sessionIdProvider.reset()
        }
    }
}