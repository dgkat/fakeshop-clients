package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map

class WebLogoutUser(private val authClient: SafeAuthenticatedApiClient) : LogoutUser {
    override suspend fun invoke(): Result<Unit, NetworkError> {
        return authClient.post<LogoutResponse, Unit>(
            path = "/api/auth/web/logout",
            body = Unit
        ).map { _ ->
            Unit
        }
    }
}