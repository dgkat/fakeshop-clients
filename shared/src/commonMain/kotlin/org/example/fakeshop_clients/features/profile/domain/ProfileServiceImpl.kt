package org.example.fakeshop_clients.features.profile.domain

import org.example.fakeshop_clients.core.auth.domain.AuthRepository

class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ProfileService {
    override suspend fun checkLoginStatus(): Boolean {
        try {
            val userInfo = profileRepository.getUserInfo()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun login(email: String, password: String): Boolean {
        try {
            if (email.isBlank() || password.isBlank()) {
                return false
            }
            val success = authRepository.login(email, password)
            return success
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun signUp(email: String, password: String): Boolean {
        try {
            if (email.isBlank() || password.isBlank()) {
                return false
            }
            val success = authRepository.signUp(email, password)
            return success
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun logout(): Boolean {
        try {
            val success = profileRepository.logout()
            return success
        } catch (e: Exception) {
            return false
        }
    }
}