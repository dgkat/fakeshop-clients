package org.example.fakeshop_clients.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class HomeViewModel() : ViewModel() {
    private val homeViewStore: HomeViewStore by lazy {
        getKoin().get<HomeViewStore> { parametersOf(viewModelScope) }
    }
    val uiState: StateFlow<HomeState> = homeViewStore.uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState.Loading
        )

    fun loadProducts() {
        homeViewStore.loadProducts()
    }

    fun onProductClick(productId: String) {
        homeViewStore.onProductClick(productId)
    }

    override fun onCleared() {
        super.onCleared()
    }
}