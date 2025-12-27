package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.LogoutRequest
import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse
import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.post
import org.example.fakeshop_clients.core.error_handling.Result

class MobileLogoutUser(
    private val authClient: SafeAuthenticatedApiClient,
    private val tokenStorage: TokenStorage
) : LogoutUser {
    override suspend operator fun invoke(): LogoutResponse {
        val refreshToken =
            tokenStorage.getRefreshToken() ?: throw Exception("No refresh token found")
        val logoutRequest = LogoutRequest(refreshToken)
        val response = authClient.post<LogoutResponse, LogoutRequest>(
            path = "/api/auth/mobile/logout",
            body = logoutRequest
        )

        when (response) {
            is Result.Error -> {
                throw Exception("Logout failed: ${response.error}")
            }
            is Result.Success -> {
                tokenStorage.clearTokens()
                return response.data
            }
        }
    }
}