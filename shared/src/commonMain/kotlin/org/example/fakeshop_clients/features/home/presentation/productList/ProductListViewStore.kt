package org.example.fakeshop_clients.features.home.presentation.productList

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.ProductListService

class ProductListViewStore(
    private val scope: CoroutineScope,
    private val productListService: ProductListService
) {
    private val _productListState = MutableStateFlow(ProductListState(isLoading = true))
    val productListState: StateFlow<ProductListState> = _productListState.asStateFlow()

    init {
        scope.launch {
            loadCategories()
        }
    }

    suspend fun loadCategories() {
        _productListState.value = ProductListState(isLoading = true)

        val accumulatedCategories = mutableListOf<CategoryRow>()

        productListService.getProducts().collect { result ->
            when (result) {
                is Result.Success -> {
                    accumulatedCategories.add(result.data)
                    _productListState.value = ProductListState(
                        categories = accumulatedCategories.toList(),
                        isLoading = true,
                        error = null
                    )
                }
                is Result.Error -> {
                    _productListState.value = ProductListState(
                        categories = accumulatedCategories.toList(),
                        isLoading = false,
                        error = HomeError.Network(result.error)
                    )
                    return@collect
                }
            }
        }

        _productListState.value = _productListState.value.copy(isLoading = false)
    }
}