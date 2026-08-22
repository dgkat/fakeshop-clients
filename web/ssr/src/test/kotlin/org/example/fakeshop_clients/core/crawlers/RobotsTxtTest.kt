package org.example.fakeshop_clients.core.crawlers

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The recommendations fragment sits one line away from `/product/like/`, which *is* disallowed, and
 * looks like exactly the same kind of thing: an HTMX endpoint under `/product/`. Tidying it into
 * that block would leave crawlers with a placeholder that never resolves — and since this project
 * serves no sitemap, the shelf's links are a real crawl-discovery path, not a redundant one.
 *
 * There is no failure signal for that mistake: the page still renders, crawlers still index it, the
 * shelf just quietly stops existing for them. Hence the guard.
 */
class RobotsTxtTest {

    private val robots: String =
        checkNotNull(javaClass.getResourceAsStream("/robots.txt")) { "robots.txt is not on the classpath" }
            .bufferedReader()
            .readText()

    /** Disallow directives, ignoring comments and blank lines. */
    private val disallowedPatterns: List<String> =
        robots.lineSequence()
            .map { it.substringBefore('#').trim() }
            .filter { it.startsWith("Disallow:", ignoreCase = true) }
            .map { it.substringAfter(':').trim() }
            .filter { it.isNotEmpty() }
            .toList()

    /**
     * The subset of robots.txt matching this file actually uses: a literal prefix, with `*` as a
     * wildcard for any run of characters.
     */
    private fun blocks(pattern: String, path: String): Boolean {
        val regex = Regex(
            "^" + pattern.split("*").joinToString(".*") { Regex.escape(it) }
        )
        return regex.containsMatchIn(path)
    }

    @Test
    fun theRecommendationsFragmentStaysCrawlable() {
        val fragment = "/en/product/socks-123/recommendations"

        val blocking = disallowedPatterns.filter { blocks(it, fragment) }
        assertTrue(
            blocking.isEmpty(),
            "the similar-products fragment must stay crawlable, but these rules block it: $blocking"
        )
    }

    @Test
    fun theLikeEndpointsStayDisallowed() {
        // The other half of the pair: the guard above must not be satisfied by someone deleting the
        // HTMX section wholesale.
        assertTrue(
            disallowedPatterns.any { blocks(it, "/product/like/socks-123") },
            "the like endpoints are write-ish HTMX calls and should stay out of the crawl budget"
        )
    }

    @Test
    fun crawlersAreOtherwiseWelcome() {
        assertTrue(robots.contains("Allow: /"), robots)
        assertFalse(
            disallowedPatterns.any { blocks(it, "/en/product/socks-123") },
            "the PDP itself must be crawlable"
        )
    }
}
