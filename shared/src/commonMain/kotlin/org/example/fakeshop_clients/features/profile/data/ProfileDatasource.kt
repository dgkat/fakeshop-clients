package org.example.fakeshop_clients.features.profile.data

import org.example.fakeshop_clients.core.auth.data.models.UserInfoResponse

interface ProfileDatasource {
    suspend fun getUserInfo(): UserInfoResponse
}