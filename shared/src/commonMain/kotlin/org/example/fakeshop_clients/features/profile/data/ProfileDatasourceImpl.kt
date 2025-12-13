package org.example.fakeshop_clients.features.profile.data

import org.example.fakeshop_clients.core.auth.data.models.UserInfoResponse
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.get

class ProfileDatasourceImpl(private val client: ApiClient) : ProfileDatasource {

    override suspend fun getUserInfo(): UserInfoResponse {
        val userInfoResponse = client.get<UserInfoResponse>(
            path = "/api/users/me"
        )
        return userInfoResponse

    }
}