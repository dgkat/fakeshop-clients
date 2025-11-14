package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse

interface AuthDatasource {
    suspend fun login(username: String, password: String): TokenRefreshResponse
    suspend fun refreshToken(refreshToken: String): TokenRefreshResponse
}