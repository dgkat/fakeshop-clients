package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.RefreshTokenRequest
import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.post

class MobileAuthDatasourceImpl(private val authClient: ApiClient) : MobileAuthDatasource {
    override suspend fun login(username: String, password: String): TokenRefreshResponse {
        val loginRequest = LoginRequest(username, password)
        val response = authClient.post<TokenRefreshResponse, LoginRequest>(
            path = "/api/auth/mobile/login",
            body = loginRequest
        )
        return response
    }

    override suspend fun refreshToken(refreshToken: String): TokenRefreshResponse {
        val refreshRequest = RefreshTokenRequest(refreshToken)
        return authClient.post<TokenRefreshResponse, RefreshTokenRequest>(
            path = "/api/auth/mobile/refresh",
            body = refreshRequest
        )
    }
}