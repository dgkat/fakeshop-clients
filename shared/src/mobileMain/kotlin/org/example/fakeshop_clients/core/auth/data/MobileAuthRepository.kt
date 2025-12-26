package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.error_handling.Result

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

        when (loginResponse) {
            is Result.Success -> {
                val accessToken = loginResponse.data.accessToken
                val refreshToken = loginResponse.data.refreshToken
                tokenStorage.saveTokens(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
                println("LoginResult Success")
                return accessToken.isNotBlank() && refreshToken.isNotBlank()
            }

            is Result.Error -> {
                loginResponse.error
                println("LoginResult Error")
                return false
            }

        }
    }
}