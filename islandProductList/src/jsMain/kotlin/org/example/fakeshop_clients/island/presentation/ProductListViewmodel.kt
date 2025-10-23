package org.example.fakeshop_clients.island.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.fakeshop_clients.features.home.presentation.models.UiBriefProduct

class ProductListViewmodel() {
    private val _uiState = MutableStateFlow(ProductListState(isLoading = true))
    val uiState: StateFlow<ProductListState> = _uiState.asStateFlow()

    fun loadCategories() {
        //scope.launch {
        try {
            console.log("[ProductListStore] Loading categories...")
            _uiState.value = ProductListState(isLoading = true)

            // Simulate loading delay
            //delay(500)

            val categories = FakeDataGenerator.getAllCategories()

            _uiState.value = ProductListState(
                categories = categories,
                isLoading = false,
                error = null
            )

            console.log("[ProductListStore] Loaded ${categories.size} categories")
        } catch (e: Exception) {
            console.error("[ProductListStore] Error loading categories:", e.message)
            _uiState.value = ProductListState(
                isLoading = false,
                error = e.message
            )
        }
        //  }
    }
}

object FakeDataGenerator {
    private val categories = listOf("Electronics", "Clothing", "Books", "Home & Garden")

    fun getCategoryProducts(category: String): List<UiBriefProduct> {
        return (1..5).map { i ->
            UiBriefProduct(
                id = "$category-$i",
                name = "$category Product $i",
                price = (10.0 + i * 5.5),
                imageUrl = "https://via.placeholder.com/600x400.png?text=$category $i",
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