package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.flatMap

class WebAuthRepository(
    private val webAuthDatasource: WebAuthDatasource
) : AuthRepository {
    override suspend fun signUp(username: String, password: String): Result<Unit, NetworkError> {
        return webAuthDatasource.signUp(username = username, password = password).flatMap { success ->
            if (success) {
                println("SignUp Success")
                Result.Success(Unit)
            } else {
                println("SignUp Error: Invalid credentials")
                Result.Error(NetworkError.Unknown("Sign up failed"))
            }
        }
    }

    override suspend fun login(username: String, password: String): Result<Unit, NetworkError> {
        return webAuthDatasource.login(username = username, password = password).flatMap { success ->
            if (success) {
                println("LoginResult Success")
                Result.Success(Unit)
            } else {
                println("LoginResult Error")
                Result.Error(NetworkError.Unknown("Login failed"))
            }
        }
    }
}