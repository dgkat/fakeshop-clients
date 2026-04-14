package org.example.fakeshop_clients.features.notifications

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.example.fakeshop_clients.features.notifications.data.PushTokenProvider
import org.w3c.dom.HTMLScriptElement
import kotlin.js.Promise

/**
 * Returns a real Firebase Cloud Messaging registration token for the browser.
 *
 * Behavior follows a "middle-ground" permission policy: this provider never
 * prompts the user. It only returns a token when `Notification.permission` is
 * already `"granted"`. The explicit permission prompt is triggered elsewhere
 * (e.g. the favorites-page banner) via [org.example.fakeshop_clients.features.notifications.data.NotificationPermissionManager].
 *
 * Firebase compat SDK is lazy-loaded from the gstatic CDN on first use, so
 * users who never enable notifications pay zero bundle cost.
 *
 * Required globals on `window`:
 *   - `__FIREBASE_CONFIG__` — Firebase web app config object
 *   - `__FIREBASE_VAPID_KEY__` — Web Push public VAPID key
 */
class WebPushTokenProvider : PushTokenProvider {

    override suspend fun getCurrentToken(): String? {
        if (getNotificationPermission() != "granted") return null

        val vapidKey = window.asDynamic().__FIREBASE_VAPID_KEY__ as? String
        if (vapidKey.isNullOrBlank() || vapidKey == "REPLACE_ME") {
            console.warn("[WebPushTokenProvider] window.__FIREBASE_VAPID_KEY__ not configured")
            return null
        }

        return try {
            ensureFirebaseInitialized()
            val swReg = ensureServiceWorkerRegistered()

            val options = js("({})")
            options.vapidKey = vapidKey
            options.serviceWorkerRegistration = swReg

            val tokenPromise = window.asDynamic()
                .firebase.messaging()
                .getToken(options)
                .unsafeCast<Promise<String?>>()
            tokenPromise.await()
        } catch (e: Throwable) {
            console.error("[WebPushTokenProvider] failed to get FCM token: ${e.message}")
            null
        }
    }

    override fun getPlatformName(): String = "web"

    private fun getNotificationPermission(): String {
        val notification = window.asDynamic().Notification
        if (notification == undefined || notification == null) return "default"
        return notification.permission as String
    }

    private suspend fun ensureFirebaseInitialized() {
        val firebase = window.asDynamic().firebase
        val alreadyInitialized = firebase != undefined &&
            firebase.apps != undefined &&
            (firebase.apps.length as Int) > 0
        if (alreadyInitialized) return

        loadScript(APP_SDK)
        loadScript(MESSAGING_SDK)

        val config = window.asDynamic().__FIREBASE_CONFIG__
        if (config == undefined || config == null) {
            throw IllegalStateException("window.__FIREBASE_CONFIG__ is not set")
        }
        window.asDynamic().firebase.initializeApp(config)
    }

    private suspend fun ensureServiceWorkerRegistered(): dynamic {
        val existing = cachedSwRegistration
        if (existing != null) return existing
        // .ready resolves to the already-active registration (e.g. from Main.kt),
        // avoiding a duplicate register() call and potential race conditions.
        val sw = window.navigator.asDynamic().serviceWorker
        val reg = (sw.ready as Promise<dynamic>).await()
        cachedSwRegistration = reg
        return reg
    }

    private suspend fun loadScript(src: String) {
        val existing = document.querySelector("script[src=\"$src\"]")
        if (existing != null) return

        val deferred = CompletableDeferred<Unit>()
        val script = document.createElement("script") as HTMLScriptElement
        script.src = src
        script.asDynamic().onload = {
            deferred.complete(Unit)
            Unit
        }
        script.asDynamic().onerror = {
            deferred.completeExceptionally(RuntimeException("Failed to load $src"))
            Unit
        }
        document.head?.appendChild(script)
        deferred.await()
    }

    companion object {
        private const val FIREBASE_VERSION = "10.12.2"
        private const val APP_SDK =
            "https://www.gstatic.com/firebasejs/$FIREBASE_VERSION/firebase-app-compat.js"
        private const val MESSAGING_SDK =
            "https://www.gstatic.com/firebasejs/$FIREBASE_VERSION/firebase-messaging-compat.js"

        private var cachedSwRegistration: dynamic = null
    }
}
