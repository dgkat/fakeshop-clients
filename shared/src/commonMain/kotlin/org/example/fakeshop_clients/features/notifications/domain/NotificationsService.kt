package org.example.fakeshop_clients.features.notifications.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.notifications.domain.models.NotificationPreferences

interface NotificationsService {

    // Preferences
    suspend fun getPreferences(): Result<NotificationPreferences, NetworkError>
    suspend fun togglePriceDrop(enabled: Boolean): Result<NotificationPreferences, NetworkError>

    // Device registration
    suspend fun registerDeviceAfterAuth()
    suspend fun unregisterDevice()
    suspend fun handleNewToken(token: String, isLoggedIn: Boolean)

    // Permission
    fun getPermissionStatus(): NotificationPermissionStatus
    suspend fun requestPermissionAndRegister(): NotificationPermissionStatus
    suspend fun onPermissionResult(granted: Boolean)
}
