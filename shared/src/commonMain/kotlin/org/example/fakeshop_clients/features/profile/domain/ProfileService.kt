package org.example.fakeshop_clients.features.profile.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

interface ProfileService {
    suspend fun checkLoginStatus(): Result<Boolean, NetworkError>
    suspend fun login(email: String, password: String): Result<Unit, NetworkError>
    suspend fun signUp(email: String, password: String): Result<Unit, NetworkError>
    suspend fun logout(deviceToken: String? = null): Result<Unit, NetworkError>
}