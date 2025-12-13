package org.example.fakeshop_clients.features.profile.data

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.features.profile.data.mappers.DataToDomainUserInfoMapper
import org.example.fakeshop_clients.features.profile.domain.ProfileRepository
import org.example.fakeshop_clients.features.profile.domain.models.UserInfo

class ProfileRepositoryImpl(
    private val profileDatasource: ProfileDatasource,
    private val logoutUser: LogoutUser,
    private val dataToDomainUserInfoMapper: DataToDomainUserInfoMapper
) : ProfileRepository {
    override suspend fun logout(): Boolean {
        return logoutUser().success
    }

    override suspend fun getUserInfo(): UserInfo {
        return dataToDomainUserInfoMapper.map(
            profileDatasource.getUserInfo()
        )
    }
}