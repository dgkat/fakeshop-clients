package org.example.fakeshop_clients.features.profile.data

import org.example.fakeshop_clients.core.auth.data.models.UserInfoResponse
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

interface ProfileDatasource {
    suspend fun getUserInfo(): Result<UserInfoResponse, NetworkError>
}