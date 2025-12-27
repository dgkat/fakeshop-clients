package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.SignUpRequest
import org.example.fakeshop_clients.core.data.models.WebAuthResponse
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map

class WebAuthDatasourceImpl(
    private val publicClient: WebSafePublicApiClient
) : WebAuthDatasource {

    override suspend fun signUp(
        username: String,
        password: String
    ): Result<Boolean, NetworkError> {
        val signUpRequest = SignUpRequest(username, password)
        return publicClient.post<WebAuthResponse, SignUpRequest>(
            path = "/api/auth/web/signup",
            body = signUpRequest
        ).map { it.success }
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<Boolean, NetworkError> {
        val loginRequest = LoginRequest(username, password)
        return publicClient.post<WebAuthResponse, LoginRequest>(
            path = "/api/auth/web/login",
            body = loginRequest
        ).map { it.success }
    }

    override suspend fun refreshToken(): Result<Boolean, NetworkError> {
        return publicClient.post<WebAuthResponse, Unit>(
            path = "/api/auth/web/refresh",
            body = Unit
        ).map { it.success }
    }
}
