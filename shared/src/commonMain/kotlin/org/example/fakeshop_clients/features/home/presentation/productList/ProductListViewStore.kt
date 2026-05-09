package org.example.fakeshop_clients.features.home.presentation.productList

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.auth.domain.isLoggedIn
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.home.domain.ProductListService
import org.example.fakeshop_clients.features.home.domain.mappers.DomainToPresentationBriefProductMapper

class ProductListViewStore(
    private val scope: CoroutineScope,
    private val productListService: ProductListService,
    private val favoritesService: FavoritesService,
    private val mapper: DomainToPresentationBriefProductMapper,
    private val sessionObserver: SessionObserver
) {
    private val _productListState = MutableStateFlow(ProductListState(isLoading = true))
    val productListState: StateFlow<ProductListState> = _productListState.asStateFlow()

    init {
        scope.launch {
            loadCategories()
        }
        scope.launch {
            favoritesService.favoritedIds.collect { ids ->
                _productListState.update { it.copy(favoritedProductIds = ids) }
            }
        }
        observeSessionChanges()
    }

    private fun observeSessionChanges() {
        sessionObserver.state
            .scan(Pair<SessionState?, SessionState?>(null, null)) { (_, prev), current -> Pair(prev, current) }
            .drop(1)
            .onEach { (previous, current) ->
                if (previous is SessionState.Authenticated && current is SessionState.Authenticated) {
                    if (!previous.role.isLoggedIn && current.role.isLoggedIn) {
                        checkBulkFavorites()
                    } else if (previous.role.isLoggedIn && !current.role.isLoggedIn) {
                        favoritesService.clearCache()
                    }
                }
            }
            .launchIn(scope)
    }

    suspend fun loadCategories() {
        _productListState.update {
            it.copy(
                categories = emptyList(),
                isLoading = true,
                error = null,
            )
        }

        val accumulatedCategories = mutableListOf<UiCategoryRow>()

        productListService.getProducts().collect { result ->
            when (result) {
                is Result.Success -> {
                    val domainCategoryRow = result.data
                    val uiCategoryRow = UiCategoryRow(
                        category = domainCategoryRow.category,
                        products = mapper.map(domainCategoryRow.products)
                    )
                    accumulatedCategories.add(uiCategoryRow)
                    _productListState.update {
                        it.copy(
                            categories = accumulatedCategories.toList(),
                            isLoading = true,
                            error = null,
                        )
                    }
                }
                is Result.Error -> {
                    _productListState.update {
                        it.copy(
                            categories = accumulatedCategories.toList(),
                            isLoading = false,
                            error = HomeError.Network(result.error),
                        )
                    }
                    return@collect
                }
            }
        }

        _productListState.update { it.copy(isLoading = false) }
        checkBulkFavorites()
    }

    private suspend fun checkBulkFavorites() {
        val allProductIds = _productListState.value.categories
            .flatMap { it.products }
            .map { it.id }

        if (allProductIds.isEmpty()) return

        favoritesService.checkBulkFavorites(allProductIds)
    }

    fun refreshFavorites() {
        scope.launch {
            favoritesService.clearCache()
            checkBulkFavorites()
        }
    }

    fun toggleFavorite(productId: String) {
        if (sessionObserver.upgradeInProgress.value) return
        val currentlyFavorited = productId in favoritesService.favoritedIds.value
        scope.launch {
            favoritesService.toggleFavorite(productId, currentlyFavorited).fold(
                onSuccess = { },
                onError = { checkBulkFavorites() }
            )
        }
    }
}
