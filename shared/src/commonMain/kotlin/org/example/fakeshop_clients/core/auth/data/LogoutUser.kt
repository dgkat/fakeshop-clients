package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

interface LogoutUser {
    suspend operator fun invoke(deviceToken: String? = null): Result<Unit, NetworkError>
}