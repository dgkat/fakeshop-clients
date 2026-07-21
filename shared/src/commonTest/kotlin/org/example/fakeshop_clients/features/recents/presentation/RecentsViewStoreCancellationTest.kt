package org.example.fakeshop_clients.features.recents.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.recents.domain.RecentsService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

/**
 * Regression coverage for item 8 (ViewStores don't cancel in-flight loads → stale-response races).
 * A slow first load superseded by a fast second load must NOT let the slow (stale) response land
 * last and clobber the newer result. Representative of the `loadJob?.cancel()` pattern applied to
 * all the load ViewStores; Recents is the cleanest seam (no init side effects, no perpetual
 * collectors).
 */
class RecentsViewStoreCancellationTest {

    private class FakeRecentsService : RecentsService {
        private data class Response(val delayMs: Long, val products: List<BriefProduct>)

        private val queue = ArrayDeque<Response>()
        var callCount = 0
            private set

        fun enqueue(delayMs: Long, products: List<BriefProduct>) {
            queue.addLast(Response(delayMs, products))
        }

        override suspend fun getRecentlyViewed(): Result<List<BriefProduct>, NetworkError> {
            callCount++
            val response = queue.removeFirst()
            delay(response.delayMs)
            return Result.Success(response.products)
        }
    }

    private fun product(id: String) = BriefProduct(
        id = id,
        name = id,
        price = 1.0,
        imageUrl = "https://example.com/$id.png",
        category = "cat"
    )

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun staleSlowLoadIsCancelledAndDoesNotOverwriteTheNewerLoad() = runTest {
        val service = FakeRecentsService()
        service.enqueue(delayMs = 1_000, products = listOf(product("stale"))) // first: slow
        service.enqueue(delayMs = 10, products = listOf(product("fresh")))    // second: fast

        val scope = CoroutineScope(StandardTestDispatcher(testScheduler))
        val store = RecentsViewStore(scope, service, DomainToPresentationBriefProductMapper())

        try {
            store.onEvent(RecentsEvent.LoadRecents)
            runCurrent() // let the slow load start and suspend inside delay(1000)

            store.onEvent(RecentsEvent.LoadRecents) // cancels the slow load, starts the fast one
            advanceUntilIdle()

            val state = store.state.value
            // The fast (newer) response wins; the slow one was cancelled mid-flight, so had it NOT
            // been cancelled it would have resumed at t≈1000 and overwritten "fresh" with "stale".
            assertEquals(listOf("fresh"), state.products.map { it.id })
            assertFalse(state.isLoading)
            assertNull(state.error)
            // both loads were dispatched; the first was simply cancelled before it could write
            assertEquals(2, service.callCount)
        } finally {
            scope.cancel()
        }
    }
}
