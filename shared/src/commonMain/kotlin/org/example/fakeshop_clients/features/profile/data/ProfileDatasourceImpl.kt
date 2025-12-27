package org.example.fakeshop_clients.features.profile.data

import org.example.fakeshop_clients.core.auth.data.models.UserInfoResponse
import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

class ProfileDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient
) : ProfileDatasource {

    override suspend fun getUserInfo(): Result<UserInfoResponse, NetworkError> {
        return authClient.get(path = "/api/users/me")
    }
}