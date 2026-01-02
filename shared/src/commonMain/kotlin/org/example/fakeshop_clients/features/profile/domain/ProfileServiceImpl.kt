package org.example.fakeshop_clients.features.profile.domain

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map

class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ProfileService {
    override suspend fun checkLoginStatus(): Result<Boolean, NetworkError> {
        return profileRepository.getUserInfo().map { userInfo ->
            true // If we got user info successfully, user is logged in
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit, NetworkError> {
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(NetworkError.Unknown("Email and password cannot be blank"))
        }
        return authRepository.login(email, password)
    }

    override suspend fun signUp(email: String, password: String): Result<Unit, NetworkError> {
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(NetworkError.Unknown("Email and password cannot be blank"))
        }
        return authRepository.signUp(email, password)
    }

    override suspend fun logout(): Result<Unit, NetworkError> {
        return profileRepository.logout()
    }
}