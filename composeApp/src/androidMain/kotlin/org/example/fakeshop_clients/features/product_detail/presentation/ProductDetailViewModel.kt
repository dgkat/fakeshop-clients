package org.example.fakeshop_clients.features.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailEvent
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailViewStore
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class ProductDetailViewModel : ViewModel() {
    private val store: ProductDetailViewStore by lazy {
        getKoin().get<ProductDetailViewStore> { parametersOf(viewModelScope) }
    }
    val state = store.state

    fun onEvent(event: ProductDetailEvent) {
        store.onEvent(event)
    }
}
