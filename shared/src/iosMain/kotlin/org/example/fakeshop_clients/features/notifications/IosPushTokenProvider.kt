package org.example.fakeshop_clients.features.notifications

import org.example.fakeshop_clients.features.notifications.data.PushTokenProvider

class IosPushTokenProvider : PushTokenProvider {

    private var cachedToken: String? = null

    fun updateToken(token: String) {
        cachedToken = token
    }

    override suspend fun getCurrentToken(): String? = cachedToken

    override fun getPlatformName(): String = "ios"
}
