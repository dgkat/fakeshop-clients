package org.example.fakeshop_clients.features.notifications.data

import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus

interface NotificationPermissionManager {
    fun getPermissionStatus(): NotificationPermissionStatus
    suspend fun requestPermission(): NotificationPermissionStatus
    fun onPermissionResult(granted: Boolean) {}
}
