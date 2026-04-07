package org.example.fakeshop_clients.core.notifications

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import org.example.fakeshop_clients.features.notifications.domain.PushTokenProvider

class AndroidPushTokenProvider : PushTokenProvider {

    override suspend fun getCurrentToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null
        }
    }

    override fun getPlatformName(): String = "android"
}
