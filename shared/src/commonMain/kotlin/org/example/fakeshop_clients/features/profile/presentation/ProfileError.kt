package org.example.fakeshop_clients.features.profile.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface ProfileError {
    data class Network(val error: NetworkError) : ProfileError
    data class Validation(val field: String, val message: String) : ProfileError
    data object NotLoggedIn : ProfileError
}
