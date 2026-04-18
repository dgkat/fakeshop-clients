package org.example.fakeshop_clients.core.notifications

import org.example.fakeshop_clients.features.notifications.domain.NotificationsService

class AndroidNotificationsService(
    private val delegate: NotificationsService,
    private val pendingDeviceTokenCache: AndroidPendingDeviceTokenCache
) : NotificationsService by delegate {

    override suspend fun registerDeviceAfterAuth() {
        pendingDeviceTokenCache.consume()?.let { pending ->
            delegate.registerDeviceToken(pending)
        }
        delegate.registerDeviceAfterAuth()
    }
}
