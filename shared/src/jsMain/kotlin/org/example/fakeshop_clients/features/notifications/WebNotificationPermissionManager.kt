package org.example.fakeshop_clients.features.notifications

import kotlinx.coroutines.await
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus
import kotlin.js.Promise

class WebNotificationPermissionManager : NotificationPermissionManager {

    override fun getPermissionStatus(): NotificationPermissionStatus {
        val permission = js("typeof Notification !== 'undefined' ? Notification.permission : 'default'") as String
        return when (permission) {
            "granted" -> NotificationPermissionStatus.GRANTED
            "denied" -> NotificationPermissionStatus.DENIED
            else -> NotificationPermissionStatus.NOT_DETERMINED
        }
    }

    override suspend fun requestPermission(): NotificationPermissionStatus {
        return try {
            val result = js("Notification.requestPermission()").unsafeCast<Promise<String>>().await()
            when (result) {
                "granted" -> NotificationPermissionStatus.GRANTED
                "denied" -> NotificationPermissionStatus.DENIED
                else -> NotificationPermissionStatus.NOT_DETERMINED
            }
        } catch (e: Exception) {
            NotificationPermissionStatus.DENIED
        }
    }
}
