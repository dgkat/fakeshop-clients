package org.example.fakeshop_clients.features.home.presentation.productList

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.home.domain.ProductListService
import org.example.fakeshop_clients.features.home.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.home.domain.models.CategoryRow

/**
 * Coverage for `loadCategories`' **best-effort / partial-load** semantics: if one category
 * call in the stream fails, the load does **not** abort — every category that succeeds is
 * still surfaced, and the trailing favorites check runs over whatever loaded.
 *
 * (This is the deliberately-chosen behavior over "stop on first error": a single failed
 * category should never blank out the whole home screen.)
 */
class ProductListViewStoreLoadCategoriesTest {

    private fun product(id: String) = BriefProduct(id, "Name-$id", 1.0, "https://img/$id.png", "cat")

    private class FakeProductListService(
        private val stream: Flow<Result<CategoryRow, NetworkError>>
    ) : ProductListService {
        override fun getProducts(): Flow<Result<CategoryRow, NetworkError>> = stream
    }

    private class RecordingFavoritesService : FavoritesService {
        val bulkChecks = mutableListOf<List<String>>()

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

        override suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError> {
            bulkChecks += productIds
            return Result.Success(emptySet())
        }

        override fun clearCache() {}
    }

    private class FakeSessionObserver : SessionObserver {
        override val state: StateFlow<SessionState> = MutableStateFlow(SessionState.Unknown)
        override val upgradeInProgress: StateFlow<Boolean> = MutableStateFlow(false)
    }

    /**
     * The store launches perpetual collectors (`favoritedIds`, session changes) in its scope,
     * so it gets a dedicated test-dispatcher scope that we cancel afterwards — otherwise those
     * never-completing coroutines would trip `runTest`'s leaked-coroutine check.
     */
    private fun TestScope.withStore(
        stream: Flow<Result<CategoryRow, NetworkError>>,
        block: (ProductListViewStore, RecordingFavoritesService) -> Unit
    ) {
        val storeScope = CoroutineScope(StandardTestDispatcher(testScheduler))
        val favorites = RecordingFavoritesService()
        val store = ProductListViewStore(
            scope = storeScope,
            productListService = FakeProductListService(stream),
            favoritesService = favorites,
            mapper = DomainToPresentationBriefProductMapper(),
            sessionObserver = FakeSessionObserver()
        )
        try {
            testScheduler.advanceUntilIdle()
            block(store, favorites)
        } finally {
            storeScope.cancel()
        }
    }

    @Test
    fun failureAmongSuccessesStillLoadsTheOtherCategories() = runTest {
        val stream = flow {
            emit(Result.Success(CategoryRow("A", listOf(product("a1")))))
            emit(Result.Error(NetworkError.Timeout))
            emit(Result.Success(CategoryRow("B", listOf(product("b1")))))
        }

        withStore(stream) { store, favorites ->
            val state = store.productListState.value

            // Best-effort: the failed category is skipped, but the load keeps going, so both
            // categories that succeeded (including the one emitted after the failure) are shown.
            assertEquals(listOf("A", "B"), state.categories.map { it.category })

            // The load runs to completion and the favorites check covers everything that loaded.
            assertTrue(!state.isLoading, "isLoading should be false once the stream completes")
            assertEquals(
                listOf(listOf("a1", "b1")),
                favorites.bulkChecks,
                "checkBulkFavorites runs once over every loaded product id"
            )
        }
    }

    @Test
    fun trailingFailureIsSurfacedButEarlierCategoriesRemain() = runTest {
        val stream = flow {
            emit(Result.Success(CategoryRow("A", listOf(product("a1")))))
            emit(Result.Success(CategoryRow("B", listOf(product("b1")))))
            emit(Result.Error(NetworkError.Timeout))
        }

        withStore(stream) { store, _ ->
            val state = store.productListState.value

            // A failure with no later success stays visible, and the already-loaded categories
            // are kept rather than blanked out.
            assertEquals(listOf("A", "B"), state.categories.map { it.category })
            val error = assertIs<HomeError.Network>(state.error)
            assertEquals(NetworkError.Timeout, error.error)
            assertTrue(!state.isLoading, "isLoading should be false after an error")
        }
    }

    @Test
    fun allSuccessesAccumulateAndRunTrailingWork() = runTest {
        val stream = flow {
            emit(Result.Success(CategoryRow("A", listOf(product("a1")))))
            emit(Result.Success(CategoryRow("B", listOf(product("b1")))))
        }

        withStore(stream) { store, favorites ->
            val state = store.productListState.value

            assertNull(state.error)
            assertEquals(listOf("A", "B"), state.categories.map { it.category })
            assertTrue(!state.isLoading, "isLoading should be false once the stream completes")
            assertEquals(
                listOf(listOf("a1", "b1")),
                favorites.bulkChecks,
                "checkBulkFavorites runs once with every loaded product id"
            )
        }
    }
}
