package org.example.fakeshop_clients.core.interactions.domain

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.fakeshop_clients.core.time.MillisClock

/**
 * Applies [SessionStaleness] over a per-platform [SessionIdStore]. Deliberately free of any
 * dependency on auth: the mobile HTTP clients call this on every request, including the ones the
 * token-refresh path issues, so a dependency back onto token storage could deadlock.
 *
 * **Session state is held in memory and persisted on a throttle.** The HTTP clients attach
 * `X-Session-Id` to *every* request — deliberately, so the header can never drift out of sync with
 * the backend's list of interaction-recording endpoints — which means this is one of the hottest
 * paths in the app. Reading and rewriting the store on each call would put a `DataStore` write (a
 * temp-file write plus fsync) on every single API call, serialized behind [mutex] so parallel
 * requests queue on disk I/O.
 *
 * So the store is read once per process and written only when the id changes or the persisted
 * timestamp has lagged by [SessionStaleness.PERSIST_THROTTLE_MILLIS]. Staleness is still evaluated
 * against the exact in-memory timestamp, so the 30-minute rule is unaffected; only durability lags,
 * and only by up to a minute on a thirty-minute window.
 *
 * Requires a single instance per process (both platforms bind it as a Koin `single`) — a second
 * instance would carry its own cache and could hand out a different id.
 */
class DefaultSessionIdProvider(
    private val store: SessionIdStore,
    private val generator: SessionIdGenerator,
    private val clock: MillisClock
) : SessionIdProvider {

    private data class Session(
        val id: String,
        val lastTouchedMillis: Long?,
        val lastPersistedMillis: Long?
    )

    private val mutex = Mutex()

    private var cached: Session? = null
    private var loaded = false

    override suspend fun current(): String = mutex.withLock {
        val now = clock.nowMillis()

        if (!loaded) {
            cached = store.read()?.let {
                Session(
                    id = it.id,
                    lastTouchedMillis = it.lastTouchedMillis,
                    lastPersistedMillis = it.lastTouchedMillis
                )
            }
            loaded = true
        }

        val session = cached
        val minted = SessionStaleness.isStale(session?.id, session?.lastTouchedMillis, now)
        val id = if (minted) generator.newId() else requireNotNull(session).id

        val persisted = session?.lastPersistedMillis
        val mustPersist = minted ||
            persisted == null ||
            now - persisted >= SessionStaleness.PERSIST_THROTTLE_MILLIS

        cached = if (mustPersist) {
            store.write(id, now)
            Session(id = id, lastTouchedMillis = now, lastPersistedMillis = now)
        } else {
            requireNotNull(session).copy(lastTouchedMillis = now)
        }

        id
    }

    override suspend fun reset() = mutex.withLock {
        cached = null
        // Stay "loaded" on purpose: if store.clear() fails, re-reading would resurrect the logged
        // out user's id and merge two people into one sequence. A null cache mints instead.
        loaded = true
        store.clear()
    }
}
