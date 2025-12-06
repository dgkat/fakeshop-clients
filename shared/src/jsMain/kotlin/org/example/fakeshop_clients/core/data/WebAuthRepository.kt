package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository

class WebAuthRepository(
    private val webAuthDatasource: WebAuthDatasource
) : AuthRepository {
    override suspend fun login(username: String, password: String): Boolean {
        return webAuthDatasource.login(username = username, password = password)
    }
}