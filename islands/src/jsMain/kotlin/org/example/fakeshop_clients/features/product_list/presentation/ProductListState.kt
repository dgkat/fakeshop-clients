package org.example.fakeshop_clients.features.product_list.presentation

import org.example.fakeshop_clients.features.home.presentation.models.UiBriefProduct

data class ProductListState(
    val categories: List<CategoryRow> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class CategoryRow(
    val category: String,
    val products: List<UiBriefProduct>
)
