package org.example.fakeshop_clients.core.extensions

import org.example.fakeshop_clients.core.interactions.domain.SessionStaleness
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * On web the staleness rule is implemented twice — in Kotlin for SSR minting and in
 * `session-id.js` for browser maintenance. Drift between the two is the designed-in weakness of
 * the hybrid: nothing fails, sessions just fragment or over-merge on one leg only.
 *
 * This pins the parts that can silently disagree.
 */
class SessionStalenessParityTest {

    private val script: String by lazy {
        File("src/main/resources/static/js/session-id.js").readText()
    }

    @Test
    fun theBrowserUsesTheSameInactivityWindowAsTheServer() {
        val declaration = Regex("""var WINDOW_MILLIS = ([^;]+);""").find(script)?.groupValues?.get(1)
        requireNotNull(declaration) { "session-id.js no longer declares WINDOW_MILLIS" }

        val jsWindowMillis = declaration
            .split('*')
            .map { it.trim().toLong() }
            .reduce(Long::times)

        assertEquals(SessionStaleness.WINDOW_MILLIS, jsWindowMillis)
    }

    @Test
    fun theBrowserTreatsAMissingIdOrTimestampAsStale() {
        assertTrue(script.contains("if (!id) return true;"))
        assertTrue(script.contains("if (touched === null || isNaN(touched)) return true;"))
    }

    @Test
    fun theBrowserComparesTheSameWayAtTheBoundary() {
        // `>` not `>=`: exactly one window of inactivity still resumes the session, on both sides.
        assertTrue(script.contains("return now - touched > WINDOW_MILLIS;"))
        assertTrue(!SessionStaleness.isStale("id", 0L, SessionStaleness.WINDOW_MILLIS))
        assertTrue(SessionStaleness.isStale("id", 0L, SessionStaleness.WINDOW_MILLIS + 1))
    }
}
