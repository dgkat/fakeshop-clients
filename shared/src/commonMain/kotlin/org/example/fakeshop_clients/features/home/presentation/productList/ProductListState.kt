package org.example.fakeshop_clients.features.home.presentation.productList

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.home.presentation.productList.HomeError

data class ProductListState(
    val categories: List<CategoryRow> = emptyList(),
    val isLoading: Boolean = true,
    val error: HomeError? = null
)

data class CategoryRow(
    val category: String,
    val products: List<UiBriefProduct>
)
