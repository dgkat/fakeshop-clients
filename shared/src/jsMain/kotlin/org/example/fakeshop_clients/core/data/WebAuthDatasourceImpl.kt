package org.example.fakeshop_clients.core.data

import kotlinx.serialization.Serializable
import org.example.fakeshop_clients.core.auth.data.models.LoginRequest
import org.example.fakeshop_clients.core.auth.data.models.SignUpRequest
import org.example.fakeshop_clients.core.data.models.WebAuthResponse
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.core.network.UrlProvider

@Serializable
private data object GuestRequest

@Serializable
private data class GuestAuthResponse(val isGuest: Boolean, val installId: String? = null)

class WebAuthDatasourceImpl(
    private val publicClient: WebSafePublicApiClient,
    private val baseUrl: UrlProvider
) : WebAuthDatasource {

    override suspend fun signUp(
        username: String,
        password: String
    ): Result<Boolean, NetworkError> {
        val signUpRequest = SignUpRequest(username, password)
        return publicClient.post<WebAuthResponse, SignUpRequest>(
            path = "${baseUrl()}/auth/signup",
            body = signUpRequest
        ).map { it.success }
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<Boolean, NetworkError> {
        val loginRequest = LoginRequest(username, password)
        return publicClient.post<WebAuthResponse, LoginRequest>(
            path = "${baseUrl()}/auth/login",
            body = loginRequest
        ).map { it.success }
    }

    override suspend fun refreshSession(): Result<Boolean, NetworkError> {
        return publicClient.post<WebAuthResponse, Unit>(
            path = "${baseUrl()}/auth/refresh",
            body = Unit
        ).map { it.success }
    }

    override suspend fun guest(installId: String?): Result<Boolean, NetworkError> {
        val headers = if (installId != null) mapOf(INSTALL_ID_HEADER to installId) else emptyMap()
        return publicClient.postWithHeaders<GuestAuthResponse, GuestRequest>(
            path = "${baseUrl()}/auth/guest",
            body = GuestRequest,
            headers = headers
        ).map { it.isGuest }
    }

    companion object {
        private const val INSTALL_ID_HEADER = "X-Install-Id"
    }
}
