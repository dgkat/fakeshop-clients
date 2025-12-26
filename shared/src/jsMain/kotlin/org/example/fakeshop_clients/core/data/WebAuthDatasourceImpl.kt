package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.SignUpRequest
import org.example.fakeshop_clients.core.data.fetchClient.PublicApiClient
import org.example.fakeshop_clients.core.data.models.WebAuthResponse
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

class WebAuthDatasourceImpl(private val publicClient: PublicApiClient) : WebAuthDatasource {
    override suspend fun signUp(username: String, password: String): Boolean {
        val signUpRequest = SignUpRequest(username, password)
        val response = publicClient.post<WebAuthResponse, SignUpRequest>(
            path = "/api/auth/web/signup",
            body = signUpRequest,
            responseType = WebAuthResponse::class
        )
        return response.success
    }

        override suspend fun login(username: String, password: String): Result<Boolean, NetworkError> {
            val loginRequest = LoginRequest(username, password)
            try {
                val response = publicClient.post<WebAuthResponse, LoginRequest>(
                    path = "/api/auth/web/login",
                    body = loginRequest,
                    responseType = WebAuthResponse::class
                )
                return Result.Success(response.success)
            }catch (e: Exception){
                return Result.Error(NetworkError())
            }
        }

        override suspend fun refreshToken(): Boolean {
            val response = publicClient.post<WebAuthResponse, Unit>(
                path = "/api/auth/web/refresh",
                body = Unit,
                responseType = WebAuthResponse::class
            )

            return response.success
        }
}