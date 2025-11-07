package org.example.fakeshop_clients.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class ProductListViewModel(
) : ViewModel() {
    private val store : ProductListViewStore by lazy {
        getKoin().get<ProductListViewStore> { parametersOf(viewModelScope) }
    }

    val uiState = store.productListState

    fun onProductClick(productId: String) {
    }

}