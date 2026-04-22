package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

interface WebAuthDatasource {
    suspend fun signUp(username: String, password: String): Result<Boolean, NetworkError>
    suspend fun login(username: String, password: String): Result<Boolean, NetworkError>
    suspend fun refreshSession(): Result<Boolean, NetworkError>
    // installId is optional on web; send it when available for idempotent retries pre-cookie.
    suspend fun guest(installId: String?): Result<Boolean, NetworkError>
}