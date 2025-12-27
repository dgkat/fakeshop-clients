package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.RefreshTokenRequest
import org.example.fakeshop_clients.core.auth.data.models.SignUpRequest
import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse
import org.example.fakeshop_clients.core.data.SafePublicApiClient
import org.example.fakeshop_clients.core.data.post
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

class MobileAuthDatasourceImpl(
    private val publicClient: SafePublicApiClient
) : MobileAuthDatasource {

    override suspend fun signUp(
        username: String,
        password: String
    ): Result<TokenRefreshResponse, NetworkError> {
        val signUpRequest = SignUpRequest(username, password)
        return publicClient.post(
            path = "/api/auth/mobile/signup",
            body = signUpRequest
        )
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<TokenRefreshResponse, NetworkError> {
        val loginRequest = LoginRequest(username, password)
        return publicClient.post(
            path = "/api/auth/mobile/login",
            body = loginRequest
        )
    }

    override suspend fun refreshToken(
        refreshToken: String
    ): Result<TokenRefreshResponse, NetworkError> {
        val refreshRequest = RefreshTokenRequest(refreshToken)
        return publicClient.post(
            path = "/api/auth/mobile/refresh",
            body = refreshRequest
        )
    }
}
