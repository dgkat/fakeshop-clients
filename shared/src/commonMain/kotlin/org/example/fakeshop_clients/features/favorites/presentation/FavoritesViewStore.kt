package org.example.fakeshop_clients.features.favorites.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.home.domain.mappers.DomainToPresentationBriefProductMapper

class FavoritesViewStore(
    private val scope: CoroutineScope,
    private val favoritesService: FavoritesService,
    private val mapper: DomainToPresentationBriefProductMapper
) {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        scope.launch {
            favoritesService.favoritedIds.collect { ids ->
                _state.update { current ->
                    if (current.products.isNotEmpty()) {
                        current.copy(products = current.products.filter { it.id in ids })
                    } else {
                        current
                    }
                }
            }
        }
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            FavoritesEvent.LoadFavorites -> loadFavorites()
            is FavoritesEvent.RemoveFavorite -> removeFavorite(event.productId)
            FavoritesEvent.Retry -> loadFavorites()
        }
    }

    private fun loadFavorites() {
        _state.update { it.copy(isLoading = true, error = null) }
        scope.launch {
            favoritesService.getFavorites().fold(
                onSuccess = { products ->
                    _state.update {
                        it.copy(products = mapper.map(products), isLoading = false, error = null)
                    }
                },
                onError = { networkError ->
                    _state.update {
                        it.copy(isLoading = false, error = FavoritesError.Network(networkError))
                    }
                }
            )
        }
    }

    private fun removeFavorite(productId: String) {
        val previousProducts = _state.value.products
        _state.update { it.copy(products = previousProducts.filter { p -> p.id != productId }) }

        scope.launch {
            favoritesService.toggleFavorite(productId, currentlyFavorited = true).fold(
                onSuccess = { },
                onError = { networkError ->
                    _state.update {
                        it.copy(
                            products = previousProducts,
                            error = FavoritesError.Network(networkError)
                        )
                    }
                    loadFavorites()
                }
            )
        }
    }
}
