package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map

class MobileAuthRepository(
    private val mobileAuthDatasource: MobileAuthDatasource,
    private val tokenStorage: TokenStorage,
    private val authApiClient: SafeAuthenticatedApiClient
) : AuthRepository {
    override suspend fun signUp(username: String, password: String): Result<Unit, NetworkError> =
        mobileAuthDatasource.signUp(username = username, password = password).map { response ->
            tokenStorage.saveTokens(accessToken = response.accessToken, refreshToken = response.refreshToken)
            authApiClient.clearTokenCache()
        }

    override suspend fun login(username: String, password: String): Result<Unit, NetworkError> =
        mobileAuthDatasource.login(username = username, password = password).map { response ->
            tokenStorage.saveTokens(accessToken = response.accessToken, refreshToken = response.refreshToken)
            authApiClient.clearTokenCache()
        }

    override suspend fun guest(installId: String): Result<Unit, NetworkError> =
        mobileAuthDatasource.guest(installId = installId).map { response ->
            tokenStorage.saveTokens(accessToken = response.accessToken, refreshToken = response.refreshToken)
        }
}