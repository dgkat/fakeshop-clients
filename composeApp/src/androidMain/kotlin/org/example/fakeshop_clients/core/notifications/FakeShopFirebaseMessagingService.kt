package org.example.fakeshop_clients.core.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.presentation.NotificationEventBus
import org.example.fakeshop_clients.features.notifications.presentation.PushNotificationEvent
import org.koin.android.ext.android.inject

class FakeShopFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationsService: NotificationsService by inject()
    private val serviceScope = CoroutineScope(SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            notificationsService.registerDeviceToken(token, "android")
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        if (data["type"] == "price_drop") {
            val productId = data["productId"] ?: return
            val title = message.notification?.title ?: "Price drop!"
            val body = message.notification?.body ?: ""

            serviceScope.launch {
                NotificationEventBus.emit(
                    PushNotificationEvent.PriceDrop(
                        productId = productId,
                        title = title,
                        body = body
                    )
                )
            }
        }
    }
}
