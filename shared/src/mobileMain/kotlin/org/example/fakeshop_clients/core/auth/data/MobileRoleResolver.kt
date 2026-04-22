package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.Role
import org.example.fakeshop_clients.core.auth.domain.RoleResolver
import org.example.fakeshop_clients.core.auth.domain.parseRoleFromJwt

class MobileRoleResolver(
    private val tokenStorage: TokenStorage
) : RoleResolver {
    override suspend fun currentRole(): Role? {
        val token = tokenStorage.getAccessToken() ?: return null
        return parseRoleFromJwt(token)
    }
}
