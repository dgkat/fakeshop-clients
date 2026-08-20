package org.example.fakeshop_clients.features.productDetailPage.presentation

import kotlinx.html.div
import kotlinx.html.stream.createHTML
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetailPage.presentation.pages.similarProductsShelf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The shelf is the only surface whose taps are attributable to the recommender itself, so the
 * `?src=` / `?pos=` on its links is the part worth pinning: nothing fails when they are missing,
 * the views just merge into the undifferentiated PRODUCT_SCREEN pile.
 */
class SimilarProductsShelfTest {

    private fun render(products: List<BriefProduct>): String = createHTML().div {
        similarProductsShelf(
            products = products,
            locale = "en",
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
    fun anEmptyShelfRendersNothingAtAll() {
        // Loading, failure and an empty response are one case: no heading, no empty-state message,
        // nothing that takes attention away from the product.
        val html = render(emptyList())

        assertEquals("<div></div>", html.trim())
    }

    @Test
    fun theShelfIsPlainAnchorsSoItSurvivesWithoutJavaScript() {
        val html = render(listOf(product("a")))

        assertTrue(html.contains("""<a href="/en/product/a"""), html)
        assertTrue(html.contains("Similar products"), html)
    }
}
