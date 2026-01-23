package org.example.fakeshop_clients.features.home.presentation.productList

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface HomeError {
    data class Network(val error: NetworkError) : HomeError
    data object NoProducts : HomeError
}