package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

interface MobileAuthDatasource {
    suspend fun signUp(username: String, password: String): Result<TokenRefreshResponse, NetworkError>
    suspend fun login(username: String, password: String): Result<TokenRefreshResponse, NetworkError>
    suspend fun refreshToken(refreshToken: String): Result<TokenRefreshResponse, NetworkError>
}