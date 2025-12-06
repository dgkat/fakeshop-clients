package org.example.fakeshop_clients.core.data

interface WebAuthDatasource {
    suspend fun login(username: String, password: String): Boolean
    suspend fun refreshToken(): Boolean
}