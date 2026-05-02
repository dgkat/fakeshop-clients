package org.example.fakeshop_clients.core.auth.data

import kotlinx.serialization.Serializable
import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.RefreshTokenRequest
import org.example.fakeshop_clients.core.auth.data.models.SignUpRequest
import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse
import org.example.fakeshop_clients.core.data.SafePublicApiClient
import org.example.fakeshop_clients.core.data.post
import org.example.fakeshop_clients.core.data.postWithHeaders
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider

class MobileAuthDatasourceImpl(
    private val publicClient: SafePublicApiClient,
    private val baseUrl: UrlProvider
) : MobileAuthDatasource {

    override suspend fun signUp(
        username: String,
        password: String
    ): Result<TokenRefreshResponse, NetworkError> {
        val signUpRequest = SignUpRequest(username, password)
        return publicClient.post(
            path = "${baseUrl()}/auth/signup",
            body = signUpRequest
        )
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<TokenRefreshResponse, NetworkError> {
        val loginRequest = LoginRequest(username, password)
        return publicClient.post(
            path = "${baseUrl()}/auth/login",
            body = loginRequest
        )
    }

    override suspend fun refreshToken(
        refreshToken: String
    ): Result<TokenRefreshResponse, NetworkError> {
        val refreshRequest = RefreshTokenRequest(refreshToken)
        return publicClient.post(
            path = "${baseUrl()}/auth/refresh",
            body = refreshRequest
        )
    }

    override suspend fun guest(installId: String): Result<TokenRefreshResponse, NetworkError> {
        return publicClient.postWithHeaders(
            path = "${baseUrl()}/auth/guest",
            body = GuestRequest,
            headers = mapOf(INSTALL_ID_HEADER to installId)
        )
    }

    companion object {
        private const val INSTALL_ID_HEADER = "X-Install-Id"
    }
}

@Serializable
private data object GuestRequest
