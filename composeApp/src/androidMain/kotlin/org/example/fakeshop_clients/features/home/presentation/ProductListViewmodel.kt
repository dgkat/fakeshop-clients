package org.example.fakeshop_clients.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore

class ProductListViewModel(
    storeFactory: (CoroutineScope) -> ProductListViewStore
) : ViewModel() {
    private val store = storeFactory(viewModelScope)
    val uiState = store.productListState

    fun toggleFavorite(productId: String) {
        store.toggleFavorite(productId)
    }
}
