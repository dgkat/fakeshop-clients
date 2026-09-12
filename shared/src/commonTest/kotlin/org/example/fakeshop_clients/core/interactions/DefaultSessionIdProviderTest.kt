package org.example.fakeshop_clients.core.interactions

import kotlinx.coroutines.test.runTest
import org.example.fakeshop_clients.core.interactions.domain.DefaultSessionIdProvider
import org.example.fakeshop_clients.core.interactions.domain.SessionIdStore
import org.example.fakeshop_clients.core.interactions.domain.SessionStaleness
import org.example.fakeshop_clients.core.interactions.domain.StoredSession
import org.example.fakeshop_clients.core.time.MillisClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeSessionIdStore(
    var stored: StoredSession? = null
) : SessionIdStore {
    var reads = 0
    var writes = 0

    override suspend fun read(): StoredSession? {
        reads++
        return stored
    }

    override suspend fun write(id: String, lastTouchedMillis: Long) {
        writes++
        stored = StoredSession(id, lastTouchedMillis)
    }

    override suspend fun clear() {
        stored = null
    }
}

private class FakeClock(var now: Long = 1_000_000L) : MillisClock {
    override fun nowMillis(): Long = now
}

private fun provider(
    store: SessionIdStore,
    clock: MillisClock
): DefaultSessionIdProvider {
    var counter = 0
    return DefaultSessionIdProvider(
        store = store,
        generator = { "id-${counter++}" },
        clock = clock
    )
}

class DefaultSessionIdProviderTest {

    private val window = SessionStaleness.WINDOW_MILLIS

    @Test
    fun reusesTheStoredIdWithinTheInactivityWindow() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        val first = sessions.current()
        clock.now += window - 1
        val second = sessions.current()

        assertEquals(first, second)
    }

    @Test
    fun mintsANewIdOnceTheInactivityWindowIsExceeded() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        val first = sessions.current()
        clock.now += window + 1
        val second = sessions.current()

        assertNotEquals(first, second)
    }

    @Test
    fun refreshesTheTimestampOnEveryReadSoAnActiveUserKeepsOneId() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        val first = sessions.current()
        // Active every 5 minutes for 3 hours — far beyond the window in total elapsed time.
        repeat(36) {
            clock.now += 5 * 60 * 1000L
            assertEquals(first, sessions.current())
        }

        assertEquals(clock.now, store.stored?.lastTouchedMillis)
    }

    @Test
    fun resetForcesANewIdOnTheNextRead() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        val first = sessions.current()
        sessions.reset()
        assertNull(store.stored)

        assertNotEquals(first, sessions.current())
    }

    @Test
    fun aStoredIdWithNoTimestampIsTreatedAsStale() = runTest {
        val store = FakeSessionIdStore(StoredSession(id = "partially-written", lastTouchedMillis = null))
        val clock = FakeClock()
        val sessions = provider(store, clock)

        assertNotEquals("partially-written", sessions.current())
    }

    @Test
    fun readsTheStoreOncePerProcessRatherThanPerCall() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        repeat(50) { sessions.current() }

        assertEquals(1, store.reads)
    }

    @Test
    fun doesNotRewriteTheStoreWithinTheThrottleWindow() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        // First call mints and must persist.
        val first = sessions.current()
        assertEquals(1, store.writes)

        // A burst of calls inside the throttle — one screen firing parallel requests.
        repeat(20) {
            clock.now += SessionStaleness.PERSIST_THROTTLE_MILLIS / 40
            assertEquals(first, sessions.current())
        }

        assertEquals(1, store.writes)
    }

    @Test
    fun persistsAgainOnceTheThrottleWindowElapses() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        sessions.current()
        assertEquals(1, store.writes)

        clock.now += SessionStaleness.PERSIST_THROTTLE_MILLIS
        sessions.current()

        assertEquals(2, store.writes)
        assertEquals(clock.now, store.stored?.lastTouchedMillis)
    }

    @Test
    fun stalenessUsesTheInMemoryClockNotTheThrottledPersistedOne() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        val first = sessions.current()

        // Active every 30s for the whole window: each step is inside the throttle, so the persisted
        // timestamp lags — but the session must not roll over, because the in-memory clock is
        // current. This is the regression the throttle could plausibly introduce.
        repeat(60) {
            clock.now += 30_000L
            assertEquals(first, sessions.current())
        }

        assertTrue(clock.now - (store.stored?.lastTouchedMillis ?: 0L) < window)
    }

    @Test
    fun aThrottledWriteStillLeavesTheStoreRecentEnoughToSurviveProcessDeath() = runTest {
        val store = FakeSessionIdStore()
        val clock = FakeClock()
        val sessions = provider(store, clock)

        val first = sessions.current()
        clock.now += SessionStaleness.PERSIST_THROTTLE_MILLIS - 1
        sessions.current()

        // Simulate a cold start: a brand new provider over the same store.
        val revived = provider(store, clock).current()

        assertEquals(first, revived)
    }

    @Test
    fun resetDoesNotResurrectTheOldIdWhenTheStoreFailsToClear() = runTest {
        val failingClear = object : SessionIdStore {
            var stored: StoredSession? = null
            override suspend fun read(): StoredSession? = stored
            override suspend fun write(id: String, lastTouchedMillis: Long) {
                stored = StoredSession(id, lastTouchedMillis)
            }
            override suspend fun clear() {
                // Store keeps the old value — a logged-out user's id must still not come back.
            }
        }
        val clock = FakeClock()
        val sessions = provider(failingClear, clock)

        val first = sessions.current()
        sessions.reset()

        assertNotEquals(first, sessions.current())
    }

    @Test
    fun stalenessBoundaryIsInclusiveOfTheWindow() {
        val now = 10_000_000L
        assertTrue(SessionStaleness.isStale(null, now, now))
        assertTrue(SessionStaleness.isStale("", now, now))
        assertTrue(SessionStaleness.isStale("id", null, now))
        assertTrue(!SessionStaleness.isStale("id", now - window, now))
        assertTrue(SessionStaleness.isStale("id", now - window - 1, now))
    }
}
