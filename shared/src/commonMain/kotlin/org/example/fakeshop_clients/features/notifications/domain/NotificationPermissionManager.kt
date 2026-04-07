package org.example.fakeshop_clients.features.notifications.domain

interface NotificationPermissionManager {
    fun getPermissionStatus(): NotificationPermissionStatus
    suspend fun requestPermission(): NotificationPermissionStatus
}
