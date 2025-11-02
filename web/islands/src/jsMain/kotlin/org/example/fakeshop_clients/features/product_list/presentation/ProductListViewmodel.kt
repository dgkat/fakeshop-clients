package org.example.fakeshop_clients.features.product_list.presentation

import kotlinx.coroutines.MainScope
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore

class ProductListViewmodel() {

    private val scope = MainScope()

    private val store = ProductListViewStore(scope = scope)

    val uiState = store.productListState

}