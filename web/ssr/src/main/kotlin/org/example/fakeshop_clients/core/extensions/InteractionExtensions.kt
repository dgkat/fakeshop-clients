package org.example.fakeshop_clients.core.extensions

import io.ktor.http.CookieEncoding
import io.ktor.server.application.ApplicationCall
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.core.interactions.domain.InteractionHeaders
import org.example.fakeshop_clients.core.interactions.domain.InteractionQuery
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.core.interactions.domain.SessionStaleness
import java.util.UUID

private const val SESSION_COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24

/**
 * Reads the browsing-session cookie, minting a new id when it is absent or stale, and writes both
 * cookies back so the browser (and the next document request) carries them.
 *
 * SSR owns *minting* because it is the only side that can act before the first document renders —
 * that closes the first-load gap and covers the JS-disabled path. `session-id.js` owns the
 * *maintenance* clock, because the server cannot see Axios/SPA activity at all. Both sides apply
 * [SessionStaleness]; the two must not drift.
 *
 * Note the per-tab rule from the backend contract is not achievable with a cookie — cookies are
 * shared across tabs. Web sessions span tabs; that is a documented deviation, not a bug.
 */
fun ApplicationCall.ensureInteractionSession(): String {
    val now = System.currentTimeMillis()
    val storedId = request.cookies[InteractionHeaders.SESSION_ID_COOKIE]
    val touched = request.cookies[InteractionHeaders.SESSION_TOUCHED_COOKIE]?.toLongOrNull()

    val id = if (SessionStaleness.isStale(storedId, touched, now)) {
        UUID.randomUUID().toString()
    } else {
        storedId!!
    }

    setInteractionCookie(InteractionHeaders.SESSION_ID_COOKIE, id)
    setInteractionCookie(InteractionHeaders.SESSION_TOUCHED_COOKIE, now.toString())
    return id
}

/**
 * The interaction attribution for this request: the session id (minted if needed) plus the
 * originating surface and position carried across the navigation as `?src=` / `?pos=`.
 */
fun ApplicationCall.interactionContext(): InteractionContext = InteractionContext(
    sessionId = runCatching { ensureInteractionSession() }.getOrNull(),
    surface = InteractionSurface.fromWireValue(
        request.queryParameters[InteractionQuery.SURFACE_PARAM]
    ),
    position = request.queryParameters[InteractionQuery.POSITION_PARAM]?.toIntOrNull()
)

/**
 * The attribution for an action taken on the product screen itself (the HTMX like/unlike
 * endpoints). Unlike a view it did not originate in a list, so the surface is always
 * [InteractionSurface.PRODUCT_SCREEN] and there is no position.
 */
fun ApplicationCall.productScreenInteractionContext(): InteractionContext = InteractionContext(
    sessionId = runCatching { ensureInteractionSession() }.getOrNull(),
    surface = InteractionSurface.PRODUCT_SCREEN
)

// Deliberately NOT HttpOnly: the Axios path needs JS to read it. This is an opaque analytics
// grouping key, not a security token — the auth session cookie is untouched by any of this.
private fun ApplicationCall.setInteractionCookie(name: String, value: String) {
    response.cookies.append(
        name = name,
        value = value,
        encoding = CookieEncoding.URI_ENCODING,
        maxAge = SESSION_COOKIE_MAX_AGE_SECONDS.toLong(),
        path = "/",
        httpOnly = false,
        extensions = mapOf("SameSite" to "Lax")
    )
}
