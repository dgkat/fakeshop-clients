package org.example.fakeshop_clients.features.recents.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface RecentsError {
    data class Network(val error: NetworkError) : RecentsError
}
