package org.example.fakeshop_clients.features.profile.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.features.profile.domain.ProfileService

class ProfileViewStore(
    private val scope: CoroutineScope,
    private val profileService: ProfileService
) {
    private val _profileState = MutableStateFlow(ProfileState(isLoading = true))
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        scope.launch {
            checkLoginStatus()
        }
    }

    private suspend fun checkLoginStatus() {
        //TODO update when
        _profileState.update { it.copy(isLoading = true) }
        try {
            val isLoggedIn = profileService.checkLoginStatus()
            _profileState.update {
                it.copy(
                    isLoggedIn = isLoggedIn,
                    isLoading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            _profileState.update {
                it.copy(
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.EmailChanged -> {
                _profileState.update { it.copy(email = event.email, error = null) }
            }

            is ProfileEvent.PasswordChanged -> {
                _profileState.update { it.copy(password = event.password, error = null) }
            }

            is ProfileEvent.LoginClicked -> {
                scope.launch { handleLogin() }
            }

            is ProfileEvent.SignUpClicked -> {
                scope.launch { handleSignUp() }
            }

            is ProfileEvent.LogoutClicked -> {
                scope.launch { handleLogout() }
            }
        }
    }

    private suspend fun handleLogin() {
        val currentState = _profileState.value
        _profileState.update { it.copy(isProcessing = true, error = null) }

        try {
            val isLoggedIn = profileService.login(currentState.email, currentState.password)

            _profileState.update {
                it.copy(
                    isLoggedIn = isLoggedIn,
                    isProcessing = false,
                    email = "",
                    password = "",
                    error = null
                )
            }
        } catch (e: Exception) {
            _profileState.update {
                it.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }

    private suspend fun handleSignUp() {
        val currentState = _profileState.value
        _profileState.update { it.copy(isProcessing = true, error = null) }

        try {
            val isLoggedIn = profileService.signUp(currentState.email, currentState.password)

            _profileState.update {
                it.copy(
                    isLoggedIn = isLoggedIn,
                    isProcessing = false,
                    email = "",
                    password = "",
                    error = null
                )
            }
        } catch (e: Exception) {
            _profileState.update {
                it.copy(
                    isLoading = false,
                    error = e.message ?: "Sign up failed"
                )
            }
        }
    }

    private suspend fun handleLogout() {
        _profileState.update { it.copy(isProcessing = true, error = null) }

        try {
            val logoutSuccess = profileService.logout()

            _profileState.update {
                it.copy(
                    isLoggedIn = false,
                    isProcessing = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            _profileState.update {
                it.copy(
                    isLoading = false,
                    error = e.message ?: "Log Out failed"
                )
            }
        }
    }

}