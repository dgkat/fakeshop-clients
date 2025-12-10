package org.example.fakeshop_clients.features.profile.data.mappers

import org.example.fakeshop_clients.core.auth.data.models.UserInfoResponse
import org.example.fakeshop_clients.features.profile.domain.models.UserInfo

class DataToDomainUserInfoMapper {
    fun map(userInfoResponse: UserInfoResponse): UserInfo {
        return UserInfo(
            name = userInfoResponse.name
        )
    }
}