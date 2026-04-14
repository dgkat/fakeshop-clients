package org.example.fakeshop_clients.features.notifications.data

interface PendingDeviceTokenCache {
    suspend fun save(token: String)
    suspend fun consume(): String?
}
