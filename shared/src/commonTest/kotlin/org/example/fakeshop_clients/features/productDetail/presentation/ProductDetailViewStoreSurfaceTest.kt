package org.example.fakeshop_clients.features.productDetail.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
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

/**
 * Pins the originating-surface rule at the ViewStore boundary: the surface a list reports when the
 * user taps an item must reach the brief-product call unchanged, and a retry must keep reporting
 * the surface the user actually came from rather than collapsing to `PRODUCT_SCREEN`.
 *
 * The failure mode this guards against is silent — every request still returns 200 and every
 * screen still renders, the `surface` column just stops carrying information.
 */
class ProductDetailViewStoreSurfaceTest {

    private class RecordingProductDetailService : ProductDetailService {
        val briefCalls = mutableListOf<Triple<String, InteractionSurface, Int?>>()

        override suspend fun getBriefProductById(
            id: String,
            surface: InteractionSurface,
            position: Int?
        ): Result<BriefProduct, NetworkError> {
            briefCalls += Triple(id, surface, position)
            return Result.Success(BriefProduct(id, "Socks", 9.99, "https://img/socks.png", "apparel"))
        }

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

    private class RecordingFavoritesService : FavoritesService {
        val toggles = mutableListOf<Triple<String, InteractionSurface, Int?>>()

        override val favoritedIds: StateFlow<Set<String>> = MutableStateFlow(emptySet())
        override suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError> =
            Result.Success(emptyList())

        override suspend fun toggleFavorite(
            productId: String,
            currentlyFavorited: Boolean,
            surface: InteractionSurface,
            position: Int?
        ): Result<Unit, NetworkError> {
            toggles += Triple(productId, surface, position)
            return Result.Success(Unit)
        }

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

    private class Harness(scope: TestScope) {
        val productDetailService = RecordingProductDetailService()
        val favoritesService = RecordingFavoritesService()
        val store = ProductDetailViewStore(
            scope = scope,
            productDetailService = productDetailService,
            bduiTemplateService = FakeBduiTemplateService(),
            bduiActionService = FakeBduiActionService(),
            replaceService = FakeReplaceService(),
            favoritesService = favoritesService,
            briefProductMapper = DomainToPresentationBriefProductMapper(),
            sessionObserver = FakeSessionObserver()
        )
    }

    @Test
    fun originatingSurfaceAndPositionReachTheBriefProductCall() = runTest {
        val h = Harness(this)

        h.store.onEvent(
            ProductDetailEvent.LoadProduct(
                productId = "socks-123",
                surface = InteractionSurface.HOME_SHELF,
                position = 3
            )
        )
        testScheduler.advanceUntilIdle()

        assertEquals(
            listOf<Triple<String, InteractionSurface, Int?>>(
                Triple("socks-123", InteractionSurface.HOME_SHELF, 3)
            ),
            h.productDetailService.briefCalls
        )
    }

    @Test
    fun aLoadWithoutAttributionReportsProductScreen() = runTest {
        val h = Harness(this)

        h.store.onEvent(ProductDetailEvent.LoadProduct("socks-123"))
        testScheduler.advanceUntilIdle()

        assertEquals(
            listOf<Triple<String, InteractionSurface, Int?>>(
                Triple("socks-123", InteractionSurface.PRODUCT_SCREEN, null)
            ),
            h.productDetailService.briefCalls
        )
    }

    @Test
    fun retryKeepsTheOriginatingSurfaceRatherThanCollapsingToProductScreen() = runTest {
        val h = Harness(this)

        h.store.onEvent(
            ProductDetailEvent.LoadProduct(
                productId = "socks-123",
                surface = InteractionSurface.SEARCH,
                position = 1
            )
        )
        testScheduler.advanceUntilIdle()

        h.store.onEvent(ProductDetailEvent.Retry)
        testScheduler.advanceUntilIdle()

        assertEquals(
            List<Triple<String, InteractionSurface, Int?>>(2) {
                Triple("socks-123", InteractionSurface.SEARCH, 1)
            },
            h.productDetailService.briefCalls
        )
    }

    @Test
    fun favoritingOnThePdpIsAlwaysAttributedToTheProductScreen() = runTest {
        val h = Harness(this)

        h.store.onEvent(
            ProductDetailEvent.LoadProduct(
                productId = "socks-123",
                surface = InteractionSurface.HOME_SHELF,
                position = 3
            )
        )
        testScheduler.advanceUntilIdle()

        h.store.onEvent(ProductDetailEvent.ToggleFavorite)
        testScheduler.advanceUntilIdle()

        // The favorite did not originate in a list — it is a tap on the product screen itself.
        assertEquals(
            listOf<Triple<String, InteractionSurface, Int?>>(
                Triple("socks-123", InteractionSurface.PRODUCT_SCREEN, null)
            ),
            h.favoritesService.toggles
        )
    }
}
