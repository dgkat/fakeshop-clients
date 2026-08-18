package org.example.fakeshop_clients.features.productDetail.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.domain.BduiActionService
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateService
import org.example.fakeshop_clients.features.bdui.domain.ReplaceService
import org.example.fakeshop_clients.features.bdui.domain.models.ActionContext
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionResponse
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding
import org.example.fakeshop_clients.features.bdui.domain.models.ResolvedReplace
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailService
import org.example.fakeshop_clients.features.recommendations.domain.RecommendationsService
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct

/**
 * Covers the client-resolved `navigate` branch of [ProductDetailViewStore.dispatchAction]:
 * a valid destination emits [ProductDetailEffect.Navigate]; anything else is a silent no-op;
 * and — mirroring the `replace` allowlist rule — `navigate` must never reach the
 * `/ui/action` service.
 */
class ProductDetailViewStoreNavigateTest {

    // --- Fakes ---

    private class FakeProductDetailService : ProductDetailService {
        /** Every brief load, with the attribution it was made under. */
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

    private class RecordingBduiActionService : BduiActionService {
        val dispatchedActionIds = mutableListOf<String>()

        override suspend fun dispatch(
            actionId: String,
            screen: String,
            templateId: String?,
            context: JsonObject,
            idempotencyKey: String?
        ): Result<BduiActionResponse, NetworkError> {
            dispatchedActionIds += actionId
            return Result.Success(BduiActionResponse())
        }
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

    private class FakeRecommendationsService : RecommendationsService {
        override suspend fun getRecommendations(
            productId: String,
            limit: Int
        ): Result<List<BriefProduct>, NetworkError> = Result.Success(emptyList())
    }

    private class FakeSessionObserver : SessionObserver {
        override val state: StateFlow<SessionState> =
            MutableStateFlow(SessionState.Unknown)
        override val upgradeInProgress: StateFlow<Boolean> = MutableStateFlow(false)
    }

    // --- Harness ---

    private class Harness(private val scope: TestScope) {
        val actionService = RecordingBduiActionService()
        val store = ProductDetailViewStore(
            scope = scope,
            productDetailService = FakeProductDetailService(),
            bduiTemplateService = FakeBduiTemplateService(),
            bduiActionService = actionService,
            replaceService = FakeReplaceService(),
            favoritesService = FakeFavoritesService(),
            recommendationsService = FakeRecommendationsService(),
            briefProductMapper = DomainToPresentationBriefProductMapper(),
            sessionObserver = FakeSessionObserver()
        )
        val effects = mutableListOf<ProductDetailEffect>()

        // Foreground collector: backgroundScope tasks are not driven by advanceUntilIdle,
        // so the subscription would never start there. Cancelled by withHarness.
        val collectorJob = scope.launch { store.effects.collect { effects += it } }

        fun loadUntilReady() {
            store.onEvent(ProductDetailEvent.LoadProduct("socks-123"))
            scope.testScheduler.advanceUntilIdle()
            assertIs<BduiBodyState.Ready>(store.state.value.bduiBodyState)
        }
    }

    private fun TestScope.withHarness(block: TestScope.(Harness) -> Unit) {
        val harness = Harness(this)
        try {
            block(harness)
        } finally {
            harness.collectorJob.cancel()
        }
    }

    private fun navigateContext(url: String?, replace: String? = null) = ActionContext(
        buildJsonObject {
            url?.let { put(BduiConstants.URL_KEY, it) }
            replace?.let { put(BduiConstants.REPLACE_STACK_KEY, it) }
        }
    )

    // --- Tests ---

    @Test
    fun validUrlEmitsNavigateEffectWithoutServerCall() = runTest {
        withHarness { h ->
            h.loadUntilReady()

            h.store.dispatchBduiAction(
                BduiConstants.NAVIGATE_ACTION_ID,
                navigateContext("/product/other-456")
            )
            testScheduler.advanceUntilIdle()

            assertEquals(
                listOf<ProductDetailEffect>(
                    ProductDetailEffect.Navigate("/product/other-456", replace = false)
                ),
                h.effects
            )
            assertTrue(
                h.actionService.dispatchedActionIds.isEmpty(),
                "navigate must never POST to /ui/action"
            )
        }
    }

    @Test
    fun replaceFlagIsCarriedOnTheEffect() = runTest {
        withHarness { h ->
            h.loadUntilReady()

            h.store.dispatchBduiAction(
                BduiConstants.NAVIGATE_ACTION_ID,
                navigateContext("/favorites", replace = "true")
            )
            testScheduler.advanceUntilIdle()

            assertEquals(
                listOf<ProductDetailEffect>(ProductDetailEffect.Navigate("/favorites", replace = true)),
                h.effects
            )
        }
    }

    @Test
    fun unknownDestinationIsASilentNoOp() = runTest {
        withHarness { h ->
            h.loadUntilReady()

            h.store.dispatchBduiAction(
                BduiConstants.NAVIGATE_ACTION_ID,
                navigateContext("/checkout")
            )
            h.store.dispatchBduiAction(
                BduiConstants.NAVIGATE_ACTION_ID,
                navigateContext("fakeshop://product/socks-123")
            )
            testScheduler.advanceUntilIdle()

            assertTrue(h.effects.isEmpty())
            assertTrue(h.actionService.dispatchedActionIds.isEmpty())
        }
    }

    @Test
    fun missingUrlIsASilentNoOp() = runTest {
        withHarness { h ->
            h.loadUntilReady()

            h.store.dispatchBduiAction(BduiConstants.NAVIGATE_ACTION_ID, navigateContext(url = null))
            testScheduler.advanceUntilIdle()

            assertTrue(h.effects.isEmpty())
            assertTrue(h.actionService.dispatchedActionIds.isEmpty())
        }
    }

    @Test
    fun navigateBeforeBodyReadyIsIgnored() = runTest {
        withHarness { h ->
            h.store.dispatchBduiAction(
                BduiConstants.NAVIGATE_ACTION_ID,
                navigateContext("/product/other-456")
            )
            testScheduler.advanceUntilIdle()

            assertTrue(h.effects.isEmpty())
        }
    }

    @Test
    fun serverActionsStillReachTheActionService() = runTest {
        withHarness { h ->
            h.loadUntilReady()

            h.store.dispatchBduiAction("selectVariant", navigateContext("/ignored"))
            testScheduler.advanceUntilIdle()

            assertEquals(listOf("selectVariant"), h.actionService.dispatchedActionIds)
        }
    }

    @Test
    fun clientResolvedReplaceDoesNotReachTheActionService() = runTest {
        withHarness { h ->
            h.loadUntilReady()

            h.store.dispatchBduiAction(
                BduiConstants.REPLACE_ACTION_ID,
                ActionContext(buildJsonObject { put(BduiConstants.TARGET_SLOT_ID_KEY, "slot-1") })
            )
            testScheduler.advanceUntilIdle()

            assertTrue(h.actionService.dispatchedActionIds.isEmpty())
        }
    }

    @Test
    fun localePrefixedAndAbsoluteUrlsAreAcceptedVerbatim() = runTest {
        // The parser validates; the effect carries the original string for the platform to map.
        // An absolute https URL is accepted too (host stripped, same parser as Universal Links) —
        // platforms only ever map the parsed route to an internal destination, never the host.
        withHarness { h ->
            h.loadUntilReady()

            h.store.dispatchBduiAction(
                BduiConstants.NAVIGATE_ACTION_ID,
                navigateContext("/en/product/other-456")
            )
            h.store.dispatchBduiAction(
                BduiConstants.NAVIGATE_ACTION_ID,
                navigateContext("https://shop.example.com/es/favorites")
            )
            testScheduler.advanceUntilIdle()

            assertEquals(
                listOf<ProductDetailEffect>(
                    ProductDetailEffect.Navigate("/en/product/other-456", replace = false),
                    ProductDetailEffect.Navigate("https://shop.example.com/es/favorites", replace = false)
                ),
                h.effects
            )
        }
    }

    /**
     * Regression for item 30: effects are a [kotlinx.coroutines.channels.Channel], not a replay-0
     * `SharedFlow`. An effect emitted while **no collector is attached** (an Android config-change
     * teardown, or the iOS observation Task re-attaching around navigation) must be buffered and
     * delivered when a collector next subscribes — the old replay-0 SharedFlow dropped it outright.
     */
    @Test
    fun effectEmittedWithNoCollectorIsBufferedAndDeliveredOnLateSubscribe() = runTest {
        val store = ProductDetailViewStore(
            scope = this,
            productDetailService = FakeProductDetailService(),
            bduiTemplateService = FakeBduiTemplateService(),
            bduiActionService = RecordingBduiActionService(),
            replaceService = FakeReplaceService(),
            favoritesService = FakeFavoritesService(),
            recommendationsService = FakeRecommendationsService(),
            briefProductMapper = DomainToPresentationBriefProductMapper(),
            sessionObserver = FakeSessionObserver()
        )

        store.onEvent(ProductDetailEvent.LoadProduct("socks-123"))
        testScheduler.advanceUntilIdle()
        assertIs<BduiBodyState.Ready>(store.state.value.bduiBodyState)

        // Emit with NO collector attached — this is the dropped-event window under a SharedFlow.
        store.dispatchBduiAction(
            BduiConstants.NAVIGATE_ACTION_ID,
            navigateContext("/product/other-456")
        )
        testScheduler.advanceUntilIdle()

        // Subscribe only afterwards; the buffered effect must still arrive.
        val received = mutableListOf<ProductDetailEffect>()
        val collectorJob = launch { store.effects.collect { received += it } }
        testScheduler.advanceUntilIdle()
        collectorJob.cancel()

        assertEquals(
            listOf<ProductDetailEffect>(ProductDetailEffect.Navigate("/product/other-456", replace = false)),
            received
        )
    }
}
