package org.example.fakeshop_clients.features.profile.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.profile.domain.models.UserInfo

interface ProfileRepository {
    suspend fun logout(): Result<Unit, NetworkError>
    suspend fun getUserInfo(): Result<UserInfo, NetworkError>
}