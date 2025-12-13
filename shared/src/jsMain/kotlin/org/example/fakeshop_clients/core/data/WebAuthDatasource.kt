package org.example.fakeshop_clients.core.data

interface WebAuthDatasource {
    suspend fun signUp(username: String, password: String): Boolean
    suspend fun login(username: String, password: String): Boolean
    suspend fun refreshToken(): Boolean
}