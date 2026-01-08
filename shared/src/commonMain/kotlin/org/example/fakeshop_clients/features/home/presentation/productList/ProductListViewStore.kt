package org.example.fakeshop_clients.features.home.presentation.productList

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.home.domain.ProductListService
import org.example.fakeshop_clients.features.home.presentation.HomeError

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
        delay(500)

        val categories = FakeDataGenerator.getAllCategories()

        // Test product list service call
        productListService.getProducts().fold(
            onSuccess = { products ->
                println("productListService accessed, size: ${products.size}")
            },
            onError = { error ->
                println("productListService error: $error")
            }
        )

        _productListState.value = ProductListState(
            categories = categories,
            isLoading = false,
            error = null
        )

        productListService.testApiCalls()
    }
}

object FakeDataGenerator {
    private val categories = listOf("Electronics", "Clothing", "Books", "Home & Garden","5th Cat")

    fun getCategoryProducts(category: String): List<UiBriefProduct> {
        return (1..5).map { i ->
            UiBriefProduct(
                id = "$category-$i",
                name = "$category Product $i",
                price = (10.0 + i * 5.5),
                imageUrl = "https://placehold.co/400x280.png?text=$category $i",
                category = category
            )
        }
    }

    fun getAllCategories(): List<CategoryRow> {
        return categories.map { category ->
            CategoryRow(
                category = category,
                products = getCategoryProducts(category)
            )
        }
    }
}