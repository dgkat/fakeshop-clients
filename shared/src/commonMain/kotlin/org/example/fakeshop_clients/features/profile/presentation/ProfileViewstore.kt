package org.example.fakeshop_clients.features.profile.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.Role
import org.example.fakeshop_clients.core.auth.domain.SessionMutator
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.auth.domain.isReal
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.profile.domain.ProfileService

class ProfileViewStore(
    private val scope: CoroutineScope,
    private val profileService: ProfileService,
    private val favoritesService: FavoritesService,
    private val notificationsService: NotificationsService,
    private val sessionMutator: SessionMutator,
    private val sessionObserver: SessionObserver
) {
    private val _profileState = MutableStateFlow(ProfileState(isLoading = true))
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        sessionObserver.state
            .onEach { state ->
                when (state) {
                    is SessionState.Authenticated -> {
                        val isReal = state.role.isReal
                        _profileState.update {
                            it.copy(
                                isLoggedIn = isReal,
                                isGuest = !isReal,
                                showNotificationsSection = isReal,
                                isLoading = false
                            )
                        }
                    }
                    SessionState.Unknown -> _profileState.update { it.copy(isLoading = true) }
                    SessionState.BootstrapFailed -> _profileState.update {
                        it.copy(
                            isLoggedIn = false,
                            isGuest = false,
                            showNotificationsSection = false,
                            isLoading = false
                        )
                    }
                }
            }
            .launchIn(scope)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.EmailChanged -> _profileState.update { it.copy(email = event.email, error = null) }
            is ProfileEvent.PasswordChanged -> _profileState.update { it.copy(password = event.password, error = null) }
            is ProfileEvent.LoginClicked -> scope.launch { handleLogin() }
            is ProfileEvent.SignUpClicked -> scope.launch { handleSignUp() }
            is ProfileEvent.LogoutClicked -> scope.launch { handleLogout() }
        }
    }

    private suspend fun handleLogin() {
        if (_profileState.value.isLoading) return
        val currentState = _profileState.value
        _profileState.update { it.copy(isProcessing = true, error = null) }
        sessionMutator.setUpgradeInProgress(true)

        profileService.login(currentState.email, currentState.password).fold(
            onSuccess = {
                registerDeviceTokenAfterAuth()
                sessionMutator.setAuthenticated(Role.LOGGED_USER)
                sessionMutator.setUpgradeInProgress(false)
                _profileState.update {
                    it.copy(
                        isLoggedIn = true,
                        isGuest = false,
                        showNotificationsSection = true,
                        isLoading = false,
                        isProcessing = false,
                        email = "",
                        password = "",
                        error = null
                    )
                }
            },
            onError = { networkError ->
                sessionMutator.setUpgradeInProgress(false)
                _profileState.update { it.copy(isProcessing = false, error = networkError.toProfileError()) }
            }
        )
    }

    private suspend fun handleSignUp() {
        if (_profileState.value.isLoading) return
        val currentState = _profileState.value
        _profileState.update { it.copy(isProcessing = true, error = null) }
        sessionMutator.setUpgradeInProgress(true)

        profileService.signUp(currentState.email, currentState.password).fold(
            onSuccess = {
                registerDeviceTokenAfterAuth()
                sessionMutator.setAuthenticated(Role.LOGGED_USER)
                sessionMutator.setUpgradeInProgress(false)
                _profileState.update {
                    it.copy(
                        isLoggedIn = true,
                        isGuest = false,
                        showNotificationsSection = true,
                        isLoading = false,
                        isProcessing = false,
                        email = "",
                        password = "",
                        error = null
                    )
                }
            },
            onError = { networkError ->
                sessionMutator.setUpgradeInProgress(false)
                _profileState.update { it.copy(isProcessing = false, error = networkError.toProfileError()) }
            }
        )
    }

    private suspend fun registerDeviceTokenAfterAuth() {
        notificationsService.registerDeviceAfterAuth()
    }

    private suspend fun handleLogout() {
        _profileState.update { it.copy(isProcessing = true, error = null) }
        val deviceToken = notificationsService.getCurrentDeviceToken()

        profileService.logout(deviceToken).fold(
            onSuccess = {
                favoritesService.clearCache()
                // Mark as guest immediately; the first subsequent authenticated request
                // will trigger fallbackToGuest (mobile) or the Axios interceptor (web)
                // to create a real guest session lazily.
                sessionMutator.setAuthenticated(Role.GUEST)
                _profileState.update { it.copy(isProcessing = false, error = null) }
            },
            onError = { networkError ->
                _profileState.update { it.copy(isProcessing = false, error = ProfileError.Network(networkError)) }
            }
        )
    }
}
