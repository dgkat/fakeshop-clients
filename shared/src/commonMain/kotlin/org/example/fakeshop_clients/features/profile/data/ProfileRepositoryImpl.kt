package org.example.fakeshop_clients.features.profile.data

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.profile.data.mappers.DataToDomainUserInfoMapper
import org.example.fakeshop_clients.features.profile.domain.ProfileRepository
import org.example.fakeshop_clients.features.profile.domain.models.UserInfo

class ProfileRepositoryImpl(
    private val profileDatasource: ProfileDatasource,
    private val logoutUser: LogoutUser,
    private val dataToDomainUserInfoMapper: DataToDomainUserInfoMapper
) : ProfileRepository {
    override suspend fun logout(deviceToken: String?): Result<Unit, NetworkError> {
        return logoutUser(deviceToken)
    }

    override suspend fun getUserInfo(): Result<UserInfo, NetworkError> {
        return profileDatasource.getUserInfo().map { userInfoResponse ->
            dataToDomainUserInfoMapper.map(userInfoResponse)
        }
    }
}