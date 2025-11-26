package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository

class MobileAuthRepository(
    private val mobileAuthDatasource: MobileAuthDatasource,
    private val authTokenProvider: AuthTokenProvider
) : AuthRepository {
    override suspend fun login(username: String, password: String): Boolean {
        val loginResponse = mobileAuthDatasource.login(username = username, password = password)

        authTokenProvider.refreshToken = loginResponse.refreshToken
        authTokenProvider.accessToken = loginResponse.accessToken
        return loginResponse.accessToken.isNotBlank() && loginResponse.refreshToken.isNotBlank()
    }
}