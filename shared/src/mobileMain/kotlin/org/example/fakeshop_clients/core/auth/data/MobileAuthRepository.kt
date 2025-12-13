package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository

class MobileAuthRepository(
    private val mobileAuthDatasource: MobileAuthDatasource,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    override suspend fun signUp(username: String, password: String): Boolean {
        val signUpResponse = mobileAuthDatasource.signUp(username = username, password = password)

        tokenStorage.saveTokens(
            accessToken = signUpResponse.accessToken,
            refreshToken = signUpResponse.refreshToken
        )
        return signUpResponse.accessToken.isNotBlank() && signUpResponse.refreshToken.isNotBlank()
    }

    override suspend fun login(username: String, password: String): Boolean {
        val loginResponse = mobileAuthDatasource.login(username = username, password = password)

        tokenStorage.saveTokens(
            accessToken = loginResponse.accessToken,
            refreshToken = loginResponse.refreshToken
        )
        return loginResponse.accessToken.isNotBlank() && loginResponse.refreshToken.isNotBlank()
    }
}