package org.example.fakeshop_clients.features.productDetailPage.presentation

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.server.application.install
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.test.runTest
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpData
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The deferred shelf's contract at the route level, which is where the two rules that matter live:
 * a recommendations failure degrades to a hidden shelf rather than a broken page, and the fragment
 * — being per-user by construction — never becomes a shared cache entry.
 *
 * A 500 here would be silent in a different way from the rest of this feature: the page itself is
 * already painted, so the only symptom is a placeholder that never resolves and an
 * `htmx:responseError` nobody is listening for.
 */
class RecommendationsFragmentRouteTest {

    private class FakeProductDetailService(
        private val recommendations: Result<List<BriefProduct>, NetworkError>
    ) : ProductDetailService {

        override suspend fun getRecommendations(
            productId: String,
            cookies: Cookies
        ): Result<List<BriefProduct>, NetworkError> = recommendations

        override suspend fun getPdpData(
            id: String,
            cookies: Cookies,
            interaction: InteractionContext
        ): Result<PdpData, NetworkError> = error("not used by the fragment route")

        override suspend fun addFavorite(
            productId: String,
            cookies: Cookies,
            interaction: InteractionContext
        ): Result<Unit, NetworkError> = error("not used by the fragment route")

        override suspend fun removeFavorite(
            productId: String,
            cookies: Cookies,
            interaction: InteractionContext
        ): Result<Unit, NetworkError> = error("not used by the fragment route")

        override suspend fun checkFavorite(
            productId: String,
            cookies: Cookies
        ): Result<Boolean, NetworkError> = error("not used by the fragment route")
    }

    @AfterTest
    fun tearDown() {
        // The Ktor plugin starts the global Koin context; leaving it running would fail the next test.
        runCatching { stopKoin() }
    }

    private fun product(id: String) = BriefProduct(
        id = id,
        name = "Product $id",
        price = 9.99,
        imageUrl = "https://example.com/$id.jpg",
        category = "shoes"
    )

    private suspend fun withService(
        recommendations: Result<List<BriefProduct>, NetworkError>,
        block: suspend ApplicationTestBuilder.() -> Unit
    ) = testApplication {
        application {
            install(Koin) {
                modules(
                    module {
                        single<ProductDetailService> { FakeProductDetailService(recommendations) }
                    }
                )
            }
            routing {
                // Mounted the way Application.kt mounts it: inside the locale group, so the shelf's
                // links keep their locale prefix.
                route("/{locale}") { productRoutes() }
            }
        }
        block()
    }

    @Test
    fun aRepositoryErrorDegradesToAnEmptyShelfRatherThanABrokenFragment() = runTest {
        withService(Result.Error(NetworkError.Timeout)) {
            val response = client.get("/en/product/socks-123/recommendations")

            assertEquals(200, response.status.value)
            val body = response.bodyAsText()
            assertFalse(
                body.contains("similar-products"),
                "a failed call must leave no shelf behind: $body"
            )
        }
    }

    @Test
    fun theFragmentIsNeverHeldInASharedCache() = runTest {
        withService(Result.Success(listOf(product("socks-1")))) {
            val response = client.get("/en/product/socks-123/recommendations")

            assertEquals(
                "private, no-store",
                response.headers[HttpHeaders.CacheControl],
                "recommendations are per-session; a shared cache entry would serve one user's shelf to another"
            )
        }
    }

    @Test
    fun theShelfComesBackAttributedAndInsideTheRequestedLocale() = runTest {
        withService(Result.Success(listOf(product("socks-1"), product("socks-2")))) {
            val body = client.get("/es/product/socks-123/recommendations").bodyAsText()

            assertTrue(body.contains("/es/product/socks-1?src=RECOMMENDATIONS&amp;pos=0"), body)
            assertTrue(body.contains("/es/product/socks-2?src=RECOMMENDATIONS&amp;pos=1"), body)
        }
    }
}
