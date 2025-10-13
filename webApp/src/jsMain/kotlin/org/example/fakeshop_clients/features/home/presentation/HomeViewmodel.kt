package org.example.fakeshop_clients.features.home.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class HomeViewmodel() {
    val scope = CoroutineScope(Dispatchers.Main + Job())
    private val homeViewStore: HomeViewStore by lazy {
        getKoin().get<HomeViewStore> { parametersOf(scope) }
    }

    val uiState: StateFlow<HomeState> = homeViewStore.uiState

    fun loadProducts() {
        homeViewStore.loadProducts()
    }

    fun onProductClick(productId: String) {
        homeViewStore.onProductClick(productId)

        console.log("Product clicked (web): $productId")
    }
}