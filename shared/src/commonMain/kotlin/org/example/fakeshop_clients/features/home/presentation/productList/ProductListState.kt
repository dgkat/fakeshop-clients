package org.example.fakeshop_clients.features.home.presentation.productList

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct

data class ProductListState(
    val categories: List<UiCategoryRow> = emptyList(),
    val isLoading: Boolean = true,
    val error: HomeError? = null,
    val favoritedProductIds: Set<String> = emptySet()
)

data class UiCategoryRow(
    val category: String,
    val products: List<UiBriefProduct>
)
