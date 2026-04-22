package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.Role
import org.example.fakeshop_clients.core.auth.domain.RoleResolver
import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.core.network.UrlProvider

class WebRoleResolver(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : RoleResolver {
    override suspend fun currentRole(): Role? {
        return authClient.get<MeResponse>(path = "${baseUrl()}/me").fold(
            onSuccess = { response ->
                response.role?.let { roleStr -> Role.entries.find { it.name == roleStr } }
            },
            onError = { null }
        )
    }
}
