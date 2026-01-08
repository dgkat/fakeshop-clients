package org.example.fakeshop_clients.core.auth.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

interface AuthRepository {
    suspend fun signUp(username: String, password: String): Result<Unit, NetworkError>
    suspend fun login(username: String, password: String): Result<Unit, NetworkError>
}