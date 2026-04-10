package org.example.fakeshop_clients.features.profile.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.domain.PendingDeviceTokenCache
import org.example.fakeshop_clients.features.notifications.domain.PushTokenProvider
import org.example.fakeshop_clients.features.profile.domain.ProfileService

class ProfileViewStore(
    private val scope: CoroutineScope,
    private val profileService: ProfileService,
    private val favoritesService: FavoritesService,
    private val notificationsService: NotificationsService,
    private val pushTokenProvider: PushTokenProvider,
    private val pendingDeviceTokenCache: PendingDeviceTokenCache
) {
    private val _profileState = MutableStateFlow(ProfileState(isLoading = true))
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        scope.launch {
            checkLoginStatus()
        }
    }

    private suspend fun checkLoginStatus() {
        _profileState.update { it.copy(isLoading = true) }

        profileService.checkLoginStatus().fold(
            onSuccess = { isLoggedIn ->
                _profileState.update {
                    it.copy(
                        isLoggedIn = isLoggedIn,
                        isLoading = false,
                        error = null
                    )
                }
            },
            onError = { networkError ->
                _profileState.update {
                    it.copy(
                        isLoggedIn = false,
                        isLoading = false,
                        error = ProfileError.Network(networkError)
                    )
                }
            }
        )
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

        profileService.login(currentState.email, currentState.password).fold(
            onSuccess = {
                registerDeviceTokenAfterAuth()
                _profileState.update {
                    it.copy(
                        isLoggedIn = true,
                        isProcessing = false,
                        email = "",
                        password = "",
                        error = null
                    )
                }
            },
            onError = { networkError ->
                _profileState.update {
                    it.copy(
                        isProcessing = false,
                        error = ProfileError.Network(networkError)
                    )
                }
            }
        )
    }

    private suspend fun handleSignUp() {
        val currentState = _profileState.value
        _profileState.update { it.copy(isProcessing = true, error = null) }

        profileService.signUp(currentState.email, currentState.password).fold(
            onSuccess = {
                registerDeviceTokenAfterAuth()
                _profileState.update {
                    it.copy(
                        isLoggedIn = true,
                        isProcessing = false,
                        email = "",
                        password = "",
                        error = null
                    )
                }
            },
            onError = { networkError ->
                _profileState.update {
                    it.copy(
                        isProcessing = false,
                        error = ProfileError.Network(networkError)
                    )
                }
            }
        )
    }

    private suspend fun registerDeviceTokenAfterAuth() {
        val platform = pushTokenProvider.getPlatformName()
        pendingDeviceTokenCache.consume()?.let { pending ->
            notificationsService.registerDeviceToken(pending, platform)
        }
        pushTokenProvider.getCurrentToken()?.let { token ->
            notificationsService.registerDeviceToken(token, platform)
        }
    }

    private suspend fun handleLogout() {
        _profileState.update { it.copy(isProcessing = true, error = null) }

        profileService.logout().fold(
            onSuccess = {
                favoritesService.clearCache()
                pushTokenProvider.getCurrentToken()?.let { token ->
                    notificationsService.removeDeviceToken(token)
                }
                _profileState.update {
                    it.copy(
                        isLoggedIn = false,
                        isProcessing = false,
                        error = null
                    )
                }
            },
            onError = { networkError ->
                _profileState.update {
                    it.copy(
                        isProcessing = false,
                        error = ProfileError.Network(networkError)
                    )
                }
            }
        )
    }

}
