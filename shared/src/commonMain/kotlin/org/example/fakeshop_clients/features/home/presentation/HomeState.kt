package org.example.fakeshop_clients.features.home.presentation

import org.example.fakeshop_clients.features.home.presentation.models.UiBriefProduct

sealed class HomeState {
    data object Loading : HomeState()
    data class Success(val products: List<UiBriefProduct>) : HomeState()
    data class Error(val message: String) : HomeState()
}