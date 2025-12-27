package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.error_handling.Result

class WebAuthRepository(
    private val webAuthDatasource: WebAuthDatasource
) : AuthRepository {
    override suspend fun signUp(username: String, password: String): Boolean {
        val signUpResponse = webAuthDatasource.signUp(username = username, password = password)

        return when (signUpResponse) {
            is Result.Success -> {
                println("SignUp Success")
                signUpResponse.data
            }
            is Result.Error -> {
                println("SignUp Error: ${signUpResponse.error}")
                false
            }
        }
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