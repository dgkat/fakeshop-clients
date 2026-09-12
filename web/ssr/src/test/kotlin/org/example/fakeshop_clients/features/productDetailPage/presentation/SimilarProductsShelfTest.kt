package org.example.fakeshop_clients.features.productDetailPage.presentation

import kotlinx.html.div
import kotlinx.html.stream.createHTML
import org.example.fakeshop_clients.core.crawlers.CrawlerDetection
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetailPage.presentation.pages.similarProductsShelf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The shelf is the only surface whose taps are attributable to the recommender itself, so the
 * `?src=` / `?pos=` on its links is the part worth pinning: nothing fails when they are missing,
 * the views just merge into the undifferentiated PRODUCT_SCREEN pile.
 *
 * Since Phase 2.5 the shelf is rendered by its own HTMX fragment rather than inline in the page,
 * so these render it the way the fragment route does.
 */
class SimilarProductsShelfTest {

    private fun render(products: List<BriefProduct>, locale: String = "en"): String =
        createHTML().div {
            similarProductsShelf(
                products = products,
                locale = locale,
                strings = mapOf("similar_products" to "Similar products")
            )
        }

    private fun product(id: String) = BriefProduct(
        id = id,
        name = "Product $id",
        price = 9.99,
        imageUrl = "https://example.com/$id.jpg",
        category = "shoes"
    )

    @Test
    fun everyLinkCarriesTheRecommendationsSurfaceAndItsRank() {
        val html = render(listOf(product("a"), product("b"), product("c")))

        assertTrue(html.contains("""href="/en/product/a?src=RECOMMENDATIONS&amp;pos=0""""), html)
        assertTrue(html.contains("""href="/en/product/b?src=RECOMMENDATIONS&amp;pos=1""""), html)
        assertTrue(html.contains("""href="/en/product/c?src=RECOMMENDATIONS&amp;pos=2""""), html)
    }

    @Test
    fun linksStayInsideTheRequestedLocale() {
        // The fragment route is registered under /{locale} precisely so this holds. Rendering it
        // without a locale would emit unprefixed links and break the hreflang set.
        val html = render(listOf(product("a")), locale = "es")

        assertTrue(html.contains("""href="/es/product/a?src=RECOMMENDATIONS"""), html)
    }

    @Test
    fun anEmptyShelfRendersNothingAtAll() {
        // Loading, failure and an empty response are one case: no heading, no empty-state message,
        // nothing that takes attention away from the product. The fragment route returns 200 with
        // this body, and hx-swap="outerHTML" then removes the placeholder entirely.
        val html = render(emptyList())

        assertEquals("<div></div>", html.trim())
    }

    @Test
    fun theShelfIsPlainAnchorsOnceItArrives() {
        val html = render(listOf(product("a")))

        assertTrue(html.contains("""<a href="/en/product/a"""), html)
        assertTrue(html.contains("Similar products"), html)
    }
}

/**
 * The trigger is the whole reason the crawler branch exists: `intersect once` saves the call for
 * the majority of sessions that never scroll to the shelf, but a bot that does not scroll would
 * never fire it, and these links are the only crawl path to related products — there is no sitemap.
 */
class CrawlerDetectionTest {

    @Test
    fun knownBotsAreDetectedRegardlessOfCase() {
        val bots = listOf(
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)",
            "Mozilla/5.0 (compatible; YandexBot/3.0)",
            "facebookexternalhit/1.1",
            "Twitterbot/1.0",
            "Mozilla/5.0 (compatible; DuckDuckBot-Https/1.1)"
        )

        bots.forEach { assertTrue(CrawlerDetection.isCrawler(it), it) }
    }

    @Test
    fun realBrowsersAreNot() {
        val browsers = listOf(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/126.0.0.0 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 " +
                "(KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:127.0) Gecko/20100101 Firefox/127.0"
        )

        browsers.forEach { assertFalse(CrawlerDetection.isCrawler(it), it) }
    }

    @Test
    fun aMissingUserAgentIsTreatedAsARealUser() {
        // Absent is not evidence of a bot, and guessing wrong costs a shared-cache entry.
        assertFalse(CrawlerDetection.isCrawler(null))
        assertFalse(CrawlerDetection.isCrawler(""))
        assertFalse(CrawlerDetection.isCrawler("   "))
    }
}
