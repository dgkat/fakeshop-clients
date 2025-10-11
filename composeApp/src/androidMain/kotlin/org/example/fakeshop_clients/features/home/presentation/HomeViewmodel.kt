package org.example.fakeshop_clients.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val homeViewStore: HomeViewStore
) : ViewModel() {

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