package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.LogoutRequest
import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse
import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.post
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.core.interactions.domain.SessionIdProvider
import org.example.fakeshop_clients.core.network.UrlProvider

class MobileLogoutUser(
    private val authClient: SafeAuthenticatedApiClient,
    private val tokenStorage: TokenStorage,
    private val baseUrl: UrlProvider,
    private val tokenCacheInvalidator: TokenCacheInvalidator,
    private val sessionIdProvider: SessionIdProvider
) : LogoutUser {
    override suspend operator fun invoke(): Result<Unit, NetworkError> {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: return Result.Error(NetworkError.Unknown("No refresh token found"))

        val logoutRequest = LogoutRequest(refreshToken)
        return authClient.post<LogoutResponse, LogoutRequest>(
            path = "${baseUrl()}/auth/logout",
            body = logoutRequest
        ).map { _ ->
            tokenStorage.clearTokens()
            tokenCacheInvalidator.invalidate()
            sessionIdProvider.reset()
        }
    }
}