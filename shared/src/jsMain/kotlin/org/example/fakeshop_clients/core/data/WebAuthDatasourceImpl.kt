package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.data.AuthDatasource
import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse

class WebAuthDatasourceImpl(private val publicClient: ApiClient) : AuthDatasource {
    //TODO clean upp unused responses
    override suspend fun login(username: String, password: String): TokenRefreshResponse {
        val loginRequest = LoginRequest(username, password)

        // Use web-specific endpoint
        val response = publicClient.post<TokenRefreshResponse, LoginRequest>(
            path = "/api/auth/web/login",
            body = loginRequest
        )

        // Note: Tokens are in cookies, not in response body
        // Return a TokenRefreshResponse with empty tokens (they're in cookies)
        return TokenRefreshResponse(
            accessToken = "",  // Not needed - in cookie
            refreshToken = "",  // Not needed - in cookie
        )
    }

    override suspend fun refreshToken(refreshToken: String): TokenRefreshResponse {
        // Use web-specific endpoint
        // Note: refreshToken parameter is ignored - it's read from cookie on backend
        val response = publicClient.post<TokenRefreshResponse, Unit>(
            path = "/api/auth/web/refresh",
            body = Unit
        )

        // Tokens are in cookies
        return TokenRefreshResponse(
            accessToken = "",  // Not needed - in cookie
            refreshToken = ""   // Not needed - in cookie
        )
    }
}