package org.example.fakeshop_clients.features.notifications.domain

interface PushTokenProvider {
    suspend fun getCurrentToken(): String?
    fun getPlatformName(): String
}
