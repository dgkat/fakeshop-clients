package org.example.fakeshop_clients.features.home.presentation.productList

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.ProductListService
import org.example.fakeshop_clients.features.home.domain.mappers.DomainToPresentationBriefProductMapper

class ProductListViewStore(
    private val scope: CoroutineScope,
    private val productListService: ProductListService,
    private val mapper: DomainToPresentationBriefProductMapper
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