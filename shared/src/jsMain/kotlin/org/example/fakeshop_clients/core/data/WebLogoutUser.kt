package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.data.models.LogoutResponse

class WebLogoutUser(private val client: ApiClient) : LogoutUser {
    override suspend fun invoke(): LogoutResponse {
        val response = client.post<LogoutResponse, Unit>(
            path = "/api/auth/web/logout",
            body = Unit
        )

        return response
    }
}