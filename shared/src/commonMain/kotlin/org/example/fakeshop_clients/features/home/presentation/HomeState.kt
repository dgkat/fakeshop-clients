package org.example.fakeshop_clients.features.home.presentation

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
//TODO replace with product list island state
sealed class HomeState {
    data object Loading : HomeState()
    data class Success(val products: List<UiBriefProduct>) : HomeState()
    data class Error(val message: String) : HomeState()
}