package org.example.fakeshop_clients.features.profile.data

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.error_handling.Result
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
        val result = profileDatasource.getUserInfo()

        return when (result) {
            is Result.Success -> {
                dataToDomainUserInfoMapper.map(result.data)
            }
            is Result.Error -> {
                println("Get user info error: ${result.error}")
                // Return a default/empty UserInfo or throw an exception
                // For now, throwing an exception to maintain existing behavior
                throw IllegalStateException("Failed to get user info: ${result.error}")
            }
        }
    }
}