package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.LogoutRequest
import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.post

class MobileLogoutUser(
    private val client: ApiClient,
    private val tokenStorage: TokenStorage
) : LogoutUser {
    override suspend operator fun invoke(): LogoutResponse {
        val refreshToken =
            tokenStorage.getRefreshToken() ?: throw Exception("No refresh token found")
        val logoutRequest = LogoutRequest(refreshToken)
        val response = client.post<LogoutResponse, LogoutRequest>(
            path = "/api/auth/mobile/logout",
            body = logoutRequest
        )

        if (response.success) {
            tokenStorage.clearTokens()
        }
        return response
    }
}