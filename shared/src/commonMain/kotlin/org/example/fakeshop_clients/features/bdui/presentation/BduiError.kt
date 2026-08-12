package org.example.fakeshop_clients.features.bdui.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface BduiError {
    data class Network(val error: NetworkError) : BduiError
    data object NotFound : BduiError
    data class ParseError(val message: String) : BduiError
}
