package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

interface WebAuthDatasource {
    suspend fun signUp(username: String, password: String): Result<Boolean, NetworkError>
    suspend fun login(username: String, password: String): Result<Boolean, NetworkError>
    suspend fun refreshToken(): Result<Boolean, NetworkError>
}