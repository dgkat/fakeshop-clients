package org.example.fakeshop_clients.features.home.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.home.domain.GetProductsUseCase

class HomeViewStore(
    private val getProductsUseCase: GetProductsUseCase,
    private val scope: CoroutineScope
) {

    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = HomeState.Loading

        scope.launch {
            getProductsUseCase().fold(
                onSuccess = { products ->
                    _uiState.value = HomeState.Success(products)
                },
                onError = { networkError ->
                    _uiState.value = HomeState.Error(HomeError.Network(networkError))
                }
            )
        }
    }

    fun onProductClick(productId: String) {
        println("Product clicked: $productId")
    }
}