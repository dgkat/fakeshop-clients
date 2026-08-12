package org.example.fakeshop_clients.features.productDetail.presentation

import org.example.fakeshop_clients.features.bdui.domain.models.ToastSeverity

sealed class ProductDetailEffect {
    data class Navigate(val url: String, val replace: Boolean = false) : ProductDetailEffect()
    data class ShowToast(val message: String, val severity: ToastSeverity) : ProductDetailEffect()
}
