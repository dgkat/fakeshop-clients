package org.example.fakeshop_clients.core.auth.data

interface TokenStorage {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    // Atomically replaces existing tokens. Use for upgrades to avoid a clear+save gap.
    suspend fun replaceTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}