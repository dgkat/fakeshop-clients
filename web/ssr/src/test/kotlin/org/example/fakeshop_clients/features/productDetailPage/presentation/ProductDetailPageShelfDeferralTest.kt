package org.example.fakeshop_clients.features.productDetailPage.presentation

import kotlinx.html.html
import kotlinx.html.stream.createHTML
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpData
import org.example.fakeshop_clients.features.productDetailPage.presentation.pages.productDetailPage
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The shelf is deferred, so the shell must ship the placeholder and *not* the shelf. Getting this
 * wrong is silent in both directions: an inline shelf quietly puts the per-user call back on the
 * page's critical path, and a missing placeholder simply means no shelf ever appears.
 */
class ProductDetailPageShelfDeferralTest {

    private fun render(locale: String = "en", isCrawler: Boolean = false): String {
        val pdpData = PdpData(
            brief = BriefProduct(
                id = "p1",
                name = "Test product",
                price = 10.0,
                imageUrl = "https://example.com/p1.jpg",
                category = "shoes"
            ),
            galleryUrls = emptyList(),
            template = BduiTemplate(
                schemaVersion = 1,
                screen = "pdp",
                category = "shoes",
                root = UiNode.Column()
            ),
            bindData = JsonObject(emptyMap())
        )
        return createHTML().html {
            productDetailPage(
                pdpData = pdpData,
                locale = locale,
                strings = mapOf("similar_products" to "Similar products"),
                stringsJson = "{}",
                isCrawler = isCrawler
            )
        }
    }

    @Test
    fun theShellShipsThePlaceholderAndNotTheShelf() {
        val html = render()

        assertTrue(html.contains("""class="similar-products-placeholder""""), html)
        assertFalse(html.contains("""class="similar-products""""), html)
        assertFalse(html.contains("Similar products"), html)
    }

    @Test
    fun theFragmentUrlIsLocalePrefixed() {
        // Without the locale the shelf's own links come back unprefixed, breaking the URL scheme.
        assertTrue(render(locale = "es").contains("""hx-get="/es/product/p1/recommendations""""))
    }

    @Test
    fun usersGetIntersectOnceSoUnscrolledSessionsNeverPayForTheCall() {
        assertTrue(render(isCrawler = false).contains("""hx-trigger="intersect once""""))
    }

    @Test
    fun crawlersGetLoadBecauseTheyCannotBeRelliedOnToScroll() {
        assertTrue(render(isCrawler = true).contains("""hx-trigger="load""""))
    }

    @Test
    fun theSwapReplacesThePlaceholderRatherThanNestingInsideIt() {
        // outerHTML is what lets an empty response remove the placeholder entirely, leaving no
        // reserved gap on products whose shelf comes back empty.
        assertTrue(render().contains("""hx-swap="outerHTML""""))
    }
}
