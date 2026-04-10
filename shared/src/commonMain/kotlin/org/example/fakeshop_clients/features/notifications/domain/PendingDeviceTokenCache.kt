package org.example.fakeshop_clients.features.notifications.domain

interface PendingDeviceTokenCache {
    suspend fun save(token: String)
    suspend fun consume(): String?
}
