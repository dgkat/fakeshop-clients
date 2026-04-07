package org.example.fakeshop_clients.features.notifications

import kotlinx.coroutines.await
import org.example.fakeshop_clients.features.notifications.domain.PushTokenProvider
import kotlin.js.Promise

class WebPushTokenProvider(
    private val vapidKey: String
) : PushTokenProvider {

    override suspend fun getCurrentToken(): String? {
        return try {
            val registration = js("navigator.serviceWorker.register('/firebase-messaging-sw.js')").unsafeCast<Promise<dynamic>>().await()
            val subscription = registration.pushManager.subscribe(
                js("{userVisibleOnly: true, applicationServerKey: urlBase64ToUint8Array(vapidKey)}")
            ).unsafeCast<Promise<dynamic>>().await()
            JSON.stringify(subscription)
        } catch (e: Exception) {
            null
        }
    }

    override fun getPlatformName(): String = "web"
}

private external fun JSON.stringify(value: dynamic): String
