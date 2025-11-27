package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository

class MobileAuthRepository(
    private val mobileAuthDatasource: MobileAuthDatasource,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    override suspend fun login(username: String, password: String): Boolean {
        val loginResponse = mobileAuthDatasource.login(username = username, password = password)

        tokenStorage.saveTokens(
            accessToken = loginResponse.accessToken,
            refreshToken = loginResponse.refreshToken
        )
        return loginResponse.accessToken.isNotBlank() && loginResponse.refreshToken.isNotBlank()
    }
}