package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.data.models.WebAuthResponse

class WebAuthDatasourceImpl(private val publicClient: ApiClient) : WebAuthDatasource {
    override suspend fun login(username: String, password: String): Boolean {
        val loginRequest = LoginRequest(username, password)
        val response = publicClient.post<WebAuthResponse, LoginRequest>(
            path = "/api/auth/web/login",
            body = loginRequest
        )
        return response.success
    }

    override suspend fun refreshToken(): Boolean {
        val response = publicClient.post<WebAuthResponse, Unit>(
            path = "/api/auth/web/refresh",
            body = Unit
        )

        return response.success
    }
}