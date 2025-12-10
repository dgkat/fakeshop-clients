package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse
import org.example.fakeshop_clients.core.auth.data.models.RefreshTokenRequest
import org.example.fakeshop_clients.core.auth.data.models.SignUpRequest
import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse
import org.example.fakeshop_clients.core.auth.data.models.UserInfoResponse
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.data.post

class MobileAuthDatasourceImpl(private val authClient: ApiClient) : MobileAuthDatasource {
    override suspend fun signUp(username: String, password: String): TokenRefreshResponse {
        val signUpRequest = SignUpRequest(username, password)
        val response = authClient.post<TokenRefreshResponse, SignUpRequest>(
            path = "/api/auth/mobile/signup",
            body = signUpRequest
        )
        return response
    }

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