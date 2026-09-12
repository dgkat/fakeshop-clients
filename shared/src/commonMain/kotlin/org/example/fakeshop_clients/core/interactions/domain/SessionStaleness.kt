package org.example.fakeshop_clients.core.interactions.domain

import kotlin.time.Duration.Companion.minutes

/**
 * The single definition of when a browsing session has gone stale.
 *
 * On web this rule is necessarily implemented twice — here (SSR minting) and again in
 * `session-id.js` (browser maintenance). Any change made here must be mirrored there, and
 * `SessionStalenessTest` pins the fixtures both sides must agree on.
 */
object SessionStaleness {

    val WINDOW = 30.minutes

    val WINDOW_MILLIS: Long = WINDOW.inWholeMilliseconds

    /**
     * How far the persisted `lastTouched` timestamp is allowed to lag the in-memory one.
     *
     * The stored copy exists only to survive process death, and a value up to this far behind
     * cannot change a [WINDOW]-length staleness verdict in any way a user could notice. Throttling
     * turns a disk write per API call into one write per minute of activity.
     *
     * Mirrors `TOUCH_THROTTLE_MILLIS` in `session-id.js`, which throttles the browser's cookie
     * writes for the same reason.
     */
    val PERSIST_THROTTLE_MILLIS: Long = 1.minutes.inWholeMilliseconds

    /**
     * A missing id or a missing/unparseable timestamp is treated as stale — a partially written
     * store must not pin one session forever.
     */
    fun isStale(storedId: String?, lastTouchedMillis: Long?, nowMillis: Long): Boolean {
        if (storedId.isNullOrBlank()) return true
        if (lastTouchedMillis == null) return true
        return nowMillis - lastTouchedMillis > WINDOW_MILLIS
    }
}
