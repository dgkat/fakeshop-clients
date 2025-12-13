package org.example.fakeshop_clients.core.auth.domain

interface AuthRepository {
    suspend fun signUp(username: String, password: String): Boolean
    suspend fun login(username: String, password: String): Boolean
}