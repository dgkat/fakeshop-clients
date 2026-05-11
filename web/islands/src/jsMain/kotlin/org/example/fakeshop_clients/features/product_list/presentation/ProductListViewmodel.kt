package org.example.fakeshop_clients.features.product_list.presentation

import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore

class ProductListViewmodel(
    private val store: ProductListViewStore
) {
    val uiState = store.productListState

    fun toggleFavorite(productId: String) {
        store.toggleFavorite(productId)
    }

    fun refreshFavorites() {
        store.refreshFavorites()
    }
}