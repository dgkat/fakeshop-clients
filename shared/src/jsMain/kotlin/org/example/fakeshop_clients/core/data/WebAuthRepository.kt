package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.error_handling.Result

class WebAuthRepository(
    private val webAuthDatasource: WebAuthDatasource
) : AuthRepository {
    override suspend fun signUp(username: String, password: String): Boolean {
        return webAuthDatasource.signUp(username = username, password = password)
    }

    override suspend fun login(username: String, password: String): Boolean {
        val loginResponse = webAuthDatasource.login(username = username, password = password)

        when (loginResponse) {
            is Result.Error -> {
                println("LoginResult Error")
                return false
            }
            is Result.Success -> {
                println("LoginResult Success")
                return loginResponse.data
            }
        }
    }
}