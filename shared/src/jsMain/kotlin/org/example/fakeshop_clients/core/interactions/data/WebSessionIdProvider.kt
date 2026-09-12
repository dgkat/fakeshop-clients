package org.example.fakeshop_clients.core.interactions.data

import org.example.fakeshop_clients.core.interactions.domain.InteractionHeaders
import org.example.fakeshop_clients.core.interactions.domain.SessionIdProvider
import org.example.fakeshop_clients.core.interactions.domain.SessionStaleness

/**
 * Cookie-backed rather than localStorage-backed (unlike [org.example.fakeshop_clients.core.auth.data.WebInstallIdProvider]),
 * because a cookie is the only store both the browser and the SSR server can read — and on web both
 * of them talk to the gateway.
 *
 * [currentSync] is the primitive: the Axios request interceptor is a plain synchronous JS callback
 * and cannot await. `document.cookie` is synchronous, so the suspend interface just delegates.
 *
 * The staleness rule here must stay in lockstep with the SSR minting path — see [SessionStaleness].
 */
class WebSessionIdProvider : SessionIdProvider {

    fun currentSync(): String {
        val now = nowMillis()
        val storedId = BrowserCookies.read(InteractionHeaders.SESSION_ID_COOKIE)
        val touched = BrowserCookies.read(InteractionHeaders.SESSION_TOUCHED_COOKIE)?.toLongOrNull()

        val id = if (SessionStaleness.isStale(storedId, touched, now)) newId() else storedId!!

        BrowserCookies.write(InteractionHeaders.SESSION_ID_COOKIE, id, COOKIE_MAX_AGE_SECONDS)
        BrowserCookies.write(
            InteractionHeaders.SESSION_TOUCHED_COOKIE,
            now.toString(),
            COOKIE_MAX_AGE_SECONDS
        )
        return id
    }

    override suspend fun current(): String = currentSync()

    override suspend fun reset() {
        BrowserCookies.remove(InteractionHeaders.SESSION_ID_COOKIE)
        BrowserCookies.remove(InteractionHeaders.SESSION_TOUCHED_COOKIE)
    }

    private fun nowMillis(): Long = js("Date.now()").unsafeCast<Double>().toLong()

    private fun newId(): String = js("crypto.randomUUID()").unsafeCast<String>()

    companion object {
        // The cookie outlives the staleness window on purpose: expiry is decided by the touched
        // timestamp, not by the browser dropping the cookie mid-sitting.
        private const val COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24
    }
}
