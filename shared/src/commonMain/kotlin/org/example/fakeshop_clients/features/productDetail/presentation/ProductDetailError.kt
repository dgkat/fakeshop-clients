package org.example.fakeshop_clients.features.productDetail.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface ProductDetailError {
    data class Network(val error: NetworkError) : ProductDetailError
    data object ProductNotFound : ProductDetailError
}
