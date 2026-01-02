package org.example.fakeshop_clients.features.search.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface SearchError {
    data class Network(val error: NetworkError) : SearchError
    data object EmptyQuery : SearchError
    data object TooShort : SearchError
}
