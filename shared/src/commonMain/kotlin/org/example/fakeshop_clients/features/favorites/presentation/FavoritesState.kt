package org.example.fakeshop_clients.features.favorites.presentation

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct

data class FavoritesState(
    val products: List<UiBriefProduct> = emptyList(),
    val isLoading: Boolean = true,
    val error: FavoritesError? = null
)
