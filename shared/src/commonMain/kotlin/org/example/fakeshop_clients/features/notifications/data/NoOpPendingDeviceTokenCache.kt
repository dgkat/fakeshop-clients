package org.example.fakeshop_clients.features.notifications.data

class NoOpPendingDeviceTokenCache : PendingDeviceTokenCache {
    override suspend fun save(token: String) = Unit
    override suspend fun consume(): String? = null
}
