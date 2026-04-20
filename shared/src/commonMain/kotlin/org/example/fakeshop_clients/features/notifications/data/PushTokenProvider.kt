package org.example.fakeshop_clients.features.notifications.data

interface PushTokenProvider {
    suspend fun getCurrentToken(): String?
    fun getPlatformName(): String
}
