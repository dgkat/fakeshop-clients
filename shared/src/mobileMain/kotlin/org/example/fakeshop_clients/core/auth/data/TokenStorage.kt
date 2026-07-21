package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.StorageError

interface TokenStorage {
    suspend fun saveTokens(accessToken: String, refreshToken: String): Result<Unit, StorageError>
    // Atomically replaces existing tokens. Use for upgrades to avoid a clear+save gap.
    suspend fun replaceTokens(accessToken: String, refreshToken: String): Result<Unit, StorageError>
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}
