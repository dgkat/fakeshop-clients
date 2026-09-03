package org.example.fakeshop_clients.features.productDetailPage.presentation

import kotlinx.html.html
import kotlinx.html.stream.createHTML
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpBody
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpData
import org.example.fakeshop_clients.features.productDetailPage.presentation.pages.productDetailPage
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * What a visitor gets when the BDUI body could not be built: the whole top half, plus a quiet
 * note instead of the server-driven section. The failure must not leak into the page — an error
 * string or a stack trace here would be worse than the truncation it replaces.
 */
class ProductDetailPageBodyFallbackTest {

    private fun render(fullDescription: String?): String {
        val pdpData = PdpData(
            brief = BriefProduct(
                id = "p1",
                name = "Legacy product",
                price = 10.0,
                imageUrl = "https://example.com/p1.jpg",
                category = "shoes"
            ),
            galleryUrls = emptyList(),
            body = PdpBody.Unavailable(
                fullDescription = fullDescription,
                error = NetworkError.HttpError(404, "Not Found", "no template for shoes")
            )
        )
        return createHTML().html {
            productDetailPage(
                pdpData = pdpData,
                locale = "en",
                strings = mapOf("product_details_unavailable" to "Details unavailable."),
                stringsJson = "{}"
            )
        }
    }

    @Test
    fun theTopHalfSurvivesAFailedBody() {
        val html = render(fullDescription = null)

        assertTrue(html.contains("Legacy product"), html)
        assertTrue(html.contains("carousel-slide"), html)
        assertTrue(html.contains("""class="product-price""""), html)
    }

    @Test
    fun theFallbackReplacesTheBduiBody() {
        val html = render(fullDescription = null)

        assertTrue(html.contains("product-body-fallback"), html)
        assertTrue(html.contains("Details unavailable."), html)
    }

    @Test
    fun proseFromTheDetailedLegIsKeptWhenItIsTheTemplateThatFailed() {
        val html = render(fullDescription = "A description that predates BDUI.")

        assertTrue(html.contains("A description that predates BDUI."), html)
    }

    @Test
    fun theBackendFailureNeverReachesThePage() {
        val html = render(fullDescription = null)

        assertFalse(html.contains("no template for shoes"), html)
        assertFalse(html.contains("Not Found"), html)
    }

    @Test
    fun theShelfIsStillRequestedBecauseItIsIndependentOfTheBody() {
        assertTrue(render(fullDescription = null).contains("similar-products-placeholder"))
    }
}
