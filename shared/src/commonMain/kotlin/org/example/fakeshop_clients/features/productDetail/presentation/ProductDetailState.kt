package org.example.fakeshop_clients.features.productDetail.presentation

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.core.presentation.models.UiDetailedProduct

data class ProductDetailState(
    val briefProduct: UiBriefProduct? = null,
    val detailedProduct: UiDetailedProduct? = null,
    val isLoadingBrief: Boolean = false,
    val isLoadingDetailed: Boolean = false,
    val error: ProductDetailError? = null
)
