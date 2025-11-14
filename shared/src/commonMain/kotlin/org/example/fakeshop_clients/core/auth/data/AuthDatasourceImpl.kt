package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.post

class AuthDatasourceImpl(private val authClient: ApiClient) : AuthDatasource {
    override suspend fun login(username: String, password: String): TokenRefreshResponse {
        val loginRequest = LoginRequest(username, password)
        val response = authClient.post<TokenRefreshResponse, LoginRequest>(
            path = "/api/auth/login",
            body = loginRequest
        )
        return response
    }
}