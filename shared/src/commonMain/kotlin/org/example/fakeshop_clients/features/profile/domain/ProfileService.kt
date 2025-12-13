package org.example.fakeshop_clients.features.profile.domain

interface ProfileService {
    suspend fun checkLoginStatus(): Boolean
    suspend fun login(email: String, password: String): Boolean
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun logout(): Boolean
}