package org.example.fakeshop_clients.features.profile.domain

import org.example.fakeshop_clients.features.profile.domain.models.UserInfo

interface ProfileRepository {
    suspend fun logout(): Boolean
    suspend fun getUserInfo(): UserInfo
}