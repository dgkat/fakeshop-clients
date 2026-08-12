package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.flatMap
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.core.error_handling.mapError

class MobileAuthRepository(
    private val mobileAuthDatasource: MobileAuthDatasource,
    private val tokenStorage: TokenStorage,
    private val tokenCacheInvalidator: TokenCacheInvalidator
) : AuthRepository {
    override suspend fun signUp(username: String, password: String): Result<Unit, NetworkError> {
        return mobileAuthDatasource.signUp(username = username, password = password)
            .flatMap { response -> persistTokens(response.accessToken, response.refreshToken) }
    }

    override suspend fun login(username: String, password: String): Result<Unit, NetworkError> {
        return mobileAuthDatasource.login(username = username, password = password)
            .flatMap { response -> persistTokens(response.accessToken, response.refreshToken) }
    }

    override suspend fun guest(installId: String): Result<Unit, NetworkError> {
        return mobileAuthDatasource.guest(installId = installId)
            .flatMap { response -> persistTokens(response.accessToken, response.refreshToken) }
    }

    private suspend fun persistTokens(
        accessToken: String,
        refreshToken: String
    ): Result<Unit, NetworkError> =
        tokenStorage.saveTokens(accessToken, refreshToken)
            .mapError { NetworkError.Unknown("Failed to persist session securely") }
            .map { tokenCacheInvalidator.invalidate() }
}
