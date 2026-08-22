package org.example.fakeshop_clients.core.crawlers

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.userAgent

/**
 * User-Agent matching for search-engine and unfurl bots.
 *
 * Used to decide how deferred content is triggered, never to change what the content *is* —
 * serving crawlers different content from users is cloaking. The PDP shelf renders the same HTML
 * either way; only the HTMX trigger differs (`load` instead of `intersect once`), because a bot's
 * scroll behaviour is not something to bet internal-link discovery on.
 *
 * Any response whose markup depends on this must be marked `private, no-store` so it cannot enter
 * a shared cache — otherwise the URL would need `Vary: User-Agent`, which defeats edge caching for
 * every real user.
 */
object CrawlerDetection {

    private val CRAWLER_TOKENS = listOf(
        "googlebot",
        "google-inspectiontool",
        "bingbot",
        "duckduckbot",
        "slurp",
        "baiduspider",
        "yandexbot",
        "applebot",
        "facebookexternalhit",
        "twitterbot",
        "linkedinbot",
        "whatsapp",
        "telegrambot",
        "petalbot"
    )

    fun isCrawler(userAgent: String?): Boolean {
        if (userAgent.isNullOrBlank()) return false
        val ua = userAgent.lowercase()
        return CRAWLER_TOKENS.any { it in ua }
    }

    fun ApplicationCall.isCrawler(): Boolean = isCrawler(request.userAgent())
}
