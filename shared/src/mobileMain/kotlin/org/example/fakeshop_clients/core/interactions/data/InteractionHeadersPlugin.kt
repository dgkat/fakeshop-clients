package org.example.fakeshop_clients.core.interactions.data

import io.ktor.client.plugins.api.createClientPlugin
import org.example.fakeshop_clients.core.interactions.domain.InteractionHeaders
import org.example.fakeshop_clients.core.interactions.domain.SessionIdProvider

class InteractionHeadersConfig {
    lateinit var sessionIdProvider: SessionIdProvider
}

/**
 * Attaches `X-Session-Id` to every outbound request.
 *
 * `createClientPlugin` + `onRequest` rather than `defaultRequest`: `onRequest` is a suspend lambda
 * and [SessionIdProvider.current] suspends (DataStore reads do), which `defaultRequest` cannot do.
 *
 * Applied to every request rather than just the interaction endpoints — it is one line, it cannot
 * drift out of sync with an endpoint list, and a stray session header elsewhere is inert.
 *
 * Analytics must never fail a user action: if the provider throws, the header is omitted. Never an
 * empty string, and never a freshly minted per-request id — the backend folds absent and blank into
 * a monitored `'unknown'` sentinel, whereas fabricated ids manufacture plausible-looking single-
 * event sessions that no guardrail excludes.
 */
val InteractionHeadersPlugin = createClientPlugin("InteractionHeaders", ::InteractionHeadersConfig) {
    val sessionIdProvider = pluginConfig.sessionIdProvider

    onRequest { request, _ ->
        val sessionId = try {
            sessionIdProvider.current()
        } catch (_: Throwable) {
            null
        }

        if (!sessionId.isNullOrBlank()) {
            request.headers[InteractionHeaders.SESSION_ID] = sessionId
        }
    }
}
