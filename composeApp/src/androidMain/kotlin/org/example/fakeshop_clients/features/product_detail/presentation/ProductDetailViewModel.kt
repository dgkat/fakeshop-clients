package org.example.fakeshop_clients.features.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.bdui.domain.models.ActionContext
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailEvent
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailViewStore

class ProductDetailViewModel(
    storeFactory: (CoroutineScope) -> ProductDetailViewStore
) : ViewModel() {
    private val store = storeFactory(viewModelScope)
    val state = store.state
    val effects = store.effects

    fun onEvent(event: ProductDetailEvent) {
        store.onEvent(event)
    }

    fun dispatchBduiAction(actionId: String, context: ActionContext) {
        store.dispatchBduiAction(actionId, context)
    }
}
