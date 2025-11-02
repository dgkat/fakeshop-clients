package org.example.fakeshop_clients.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore

class ProductListViewModel(
) : ViewModel() {
    private val store = ProductListViewStore(
        scope = viewModelScope
    )

    val uiState = store.productListState

    fun onProductClick(productId: String) {
    }

}