package org.example.fakeshop_clients.features.home.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
            try {
                val products = getProductsUseCase()
                _uiState.value = HomeState.Success(products)
            } catch (e: Exception) {
                _uiState.value = HomeState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun onProductClick(productId: String) {
        println("Product clicked: $productId")
    }
}