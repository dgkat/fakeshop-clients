package org.example.fakeshop_clients.features.recents.presentation

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct

data class RecentsState(
    val products: List<UiBriefProduct> = emptyList(),
    val isLoading: Boolean = true,
    val error: RecentsError? = null
)
