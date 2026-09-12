package org.example.fakeshop_clients.features.productDetail.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.domain.BduiActionService
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateService
import org.example.fakeshop_clients.features.bdui.domain.ReplaceService
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionResponse
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding
import org.example.fakeshop_clients.features.bdui.domain.models.ResolvedReplace
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct
import org.example.fakeshop_clients.features.recommendations.domain.RecommendationsService

/**
 * The "similar products" shelf is a third, non-blocking leg of the product load. What these tests
 * protect is the *asymmetry*: a recommendations failure must cost nothing — the product page stays
 * fully usable and the shelf simply isn't there — while a stale response from a product the user
 * has already navigated away from must never land in the newer product's state.
 */
class ProductDetailViewStoreRecommendationsTest {

    private fun product(id: String) =
        BriefProduct(id, "Socks $id", 9.99, "https://img/$id.png", "apparel")

    private class FakeProductDetailService : ProductDetailService {
        override suspend fun getBriefProductById(
            id: String,
            surface: InteractionSurface,
            position: Int?
        ): Result<BriefProduct, NetworkError> =
            Result.Success(BriefProduct(id, "Socks", 9.99, "https://img/socks.png", "apparel"))

        override suspend fun getDetailedProductById(id: String): Result<DetailedProduct, NetworkError> =
            Result.Success(
                DetailedProduct(id, "apparel", "Great socks", emptyList(), buildJsonObject { })
            )
    }

    private class FakeBduiTemplateService : BduiTemplateService {
        override suspend fun getPdpTemplate(category: String): Result<BduiTemplate, NetworkError> =
            Result.Success(
                BduiTemplate(
                    schemaVersion = BduiConstants.CURRENT_SCHEMA_VERSION,
                    screen = "pdp",
                    category = category,
                    root = UiNode.Column()
                )
            )
    }

    private class FakeBduiActionService : BduiActionService {
        override suspend fun dispatch(
            actionId: String,
            screen: String,
            templateId: String?,
            context: JsonObject,
            idempotencyKey: String?
        ): Result<BduiActionResponse, NetworkError> = Result.Success(BduiActionResponse())
    }

    private class FakeReplaceService : ReplaceService {
        override suspend fun getReplaceBindings(productId: String): Result<List<ReplaceBinding>, NetworkError> =
            Result.Success(emptyList())

        override suspend fun resolve(
            bindings: List<ReplaceBinding>,
            targetSlotId: String
        ): Result<ResolvedReplace?, NetworkError> = Result.Success(null)
    }

    private class FakeFavoritesService : FavoritesService {
        override val favoritedIds: StateFlow<Set<String>> = MutableStateFlow(emptySet())
        override suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError> =
            Result.Success(emptyList())

        override suspend fun toggleFavorite(
            productId: String,
            currentlyFavorited: Boolean,
            surface: InteractionSurface,
            position: Int?
        ): Result<Unit, NetworkError> = Result.Success(Unit)

        override suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError> =
            Result.Success(false)

        override suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError> =
            Result.Success(emptySet())

        override fun clearCache() {}
    }

    private class FakeSessionObserver : SessionObserver {
        override val state: StateFlow<SessionState> = MutableStateFlow(SessionState.Unknown)
        override val upgradeInProgress: StateFlow<Boolean> = MutableStateFlow(false)
    }

    /** Answers per seed product, so a slow first product can be left in flight on purpose. */
    private class ScriptedRecommendationsService(
        private val answers: Map<String, Result<List<BriefProduct>, NetworkError>>,
        private val gates: Map<String, CompletableDeferred<Unit>> = emptyMap()
    ) : RecommendationsService {
        val seenProductIds = mutableListOf<String>()

        override suspend fun getRecommendations(
            productId: String,
            limit: Int
        ): Result<List<BriefProduct>, NetworkError> {
            seenProductIds += productId
            gates[productId]?.await()
            return answers[productId] ?: Result.Success(emptyList())
        }
    }

    private fun storeWith(recommendations: RecommendationsService, scope: TestScope) =
        ProductDetailViewStore(
            scope = scope,
            productDetailService = FakeProductDetailService(),
            bduiTemplateService = FakeBduiTemplateService(),
            bduiActionService = FakeBduiActionService(),
            replaceService = FakeReplaceService(),
            favoritesService = FakeFavoritesService(),
            recommendationsService = recommendations,
            briefProductMapper = DomainToPresentationBriefProductMapper(),
            sessionObserver = FakeSessionObserver()
        )

    @Test
    fun successPopulatesTheShelf() = runTest {
        val store = storeWith(
            ScriptedRecommendationsService(
                mapOf("socks-123" to Result.Success(listOf(product("a"), product("b"))))
            ),
            this
        )

        store.onEvent(ProductDetailEvent.LoadProduct("socks-123"))
        testScheduler.advanceUntilIdle()

        assertEquals(listOf("a", "b"), store.state.value.recommendations.map { it.id })
    }

    @Test
    fun failureHidesTheShelfAndLeavesTheProductPageUsable() = runTest {
        val store = storeWith(
            ScriptedRecommendationsService(
                mapOf("socks-123" to Result.Error(NetworkError.NoConnection))
            ),
            this
        )

        store.onEvent(ProductDetailEvent.LoadProduct("socks-123"))
        testScheduler.advanceUntilIdle()

        assertTrue(store.state.value.recommendations.isEmpty())
        // The product itself is unaffected — no error state, body rendered.
        assertIs<BriefProductState.Success>(store.state.value.briefState)
        assertIs<BduiBodyState.Ready>(store.state.value.bduiBodyState)
    }

    @Test
    fun anEmptyResponseHidesTheShelf() = runTest {
        val store = storeWith(
            ScriptedRecommendationsService(mapOf("socks-123" to Result.Success(emptyList()))),
            this
        )

        store.onEvent(ProductDetailEvent.LoadProduct("socks-123"))
        testScheduler.advanceUntilIdle()

        assertTrue(store.state.value.recommendations.isEmpty())
    }

    @Test
    fun navigatingToAnotherProductCancelsTheInFlightRequestAndNoStaleShelfLands() = runTest {
        val firstProductGate = CompletableDeferred<Unit>()
        val service = ScriptedRecommendationsService(
            answers = mapOf(
                "socks-123" to Result.Success(listOf(product("stale"))),
                "socks-456" to Result.Success(listOf(product("fresh")))
            ),
            gates = mapOf("socks-123" to firstProductGate)
        )
        val store = storeWith(service, this)

        store.onEvent(ProductDetailEvent.LoadProduct("socks-123"))
        testScheduler.advanceUntilIdle()

        store.onEvent(ProductDetailEvent.LoadProduct("socks-456"))
        testScheduler.advanceUntilIdle()

        // Release the first product's response only after the user has moved on.
        firstProductGate.complete(Unit)
        testScheduler.advanceUntilIdle()

        assertEquals(listOf("socks-123", "socks-456"), service.seenProductIds)
        assertEquals(listOf("fresh"), store.state.value.recommendations.map { it.id })
    }
}
