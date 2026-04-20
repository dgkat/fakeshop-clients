package org.example.fakeshop_clients.features.notifications.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.notifications.data.NotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.data.PushTokenProvider
import org.example.fakeshop_clients.features.notifications.domain.models.NotificationPreferences

class NotificationsServiceImpl(
    private val repository: NotificationsRepository,
    private val pushTokenProvider: PushTokenProvider,
    private val permissionManager: NotificationPermissionManager
) : NotificationsService {

    override suspend fun getPreferences(): Result<NotificationPreferences, NetworkError> {
        return repository.getPreferences()
    }

    override suspend fun togglePriceDrop(enabled: Boolean): Result<NotificationPreferences, NetworkError> {
        return repository.updatePreferences(priceDropEnabled = enabled)
    }

    override suspend fun registerDeviceAfterAuth() {
        registerCurrentDevice()
    }

    override suspend fun registerDeviceToken(token: String) {
        repository.registerDeviceToken(token, pushTokenProvider.getPlatformName())
    }

    override suspend fun unregisterDevice() {
        pushTokenProvider.getCurrentToken()?.let { token ->
            repository.removeDeviceToken(token)
        }
    }

    override fun getPermissionStatus(): NotificationPermissionStatus {
        return permissionManager.getPermissionStatus()
    }

    override suspend fun requestPermissionAndRegister(): NotificationPermissionStatus {
        val status = permissionManager.requestPermission()
        if (status == NotificationPermissionStatus.GRANTED) {
            registerCurrentDevice()
        }
        return status
    }

    override suspend fun onPermissionResult(granted: Boolean) {
        permissionManager.onPermissionResult(granted)
        if (granted) {
            registerCurrentDevice()
        }
    }

    private suspend fun registerCurrentDevice() {
        pushTokenProvider.getCurrentToken()?.let { token ->
            repository.registerDeviceToken(token, pushTokenProvider.getPlatformName())
        }
    }
}
