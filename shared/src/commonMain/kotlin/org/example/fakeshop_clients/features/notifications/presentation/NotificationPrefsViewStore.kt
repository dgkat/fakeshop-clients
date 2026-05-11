package org.example.fakeshop_clients.features.notifications.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.auth.domain.isLoggedIn
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService

class NotificationPrefsViewStore(
    private val scope: CoroutineScope,
    private val notificationsService: NotificationsService,
    private val sessionObserver: SessionObserver
) {
    private val _state = MutableStateFlow(NotificationPrefsState())
    val state: StateFlow<NotificationPrefsState> = _state.asStateFlow()

    init {
        sessionObserver.state
            .onEach { session ->
                when (session) {
                    is SessionState.Authenticated -> {
                        if (session.role.isLoggedIn) loadPreferences()
                        else _state.value = NotificationPrefsState(isLoading = false)
                    }
                    SessionState.BootstrapFailed,
                    SessionState.Unknown -> Unit
                }
            }
            .launchIn(scope)

        sessionObserver.upgradeInProgress
            .onEach { inProgress -> _state.update { it.copy(writesBlocked = inProgress) } }
            .launchIn(scope)
    }

    fun onEvent(event: NotificationPrefsEvent) {
        when (event) {
            is NotificationPrefsEvent.LoadPreferences -> loadPreferences()
            is NotificationPrefsEvent.TogglePriceDrop -> togglePriceDrop(event.enabled)
        }
    }

    private fun loadPreferences() {
        _state.update { it.copy(isLoading = true, error = null) }
        scope.launch {
            notificationsService.getPreferences().fold(
                onSuccess = { prefs ->
                    _state.update {
                        it.copy(
                            priceDropEnabled = prefs.priceDropEnabled,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onError = { networkError ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = NotificationPrefsError.Network(networkError)
                        )
                    }
                }
            )
        }
    }

    private fun togglePriceDrop(enabled: Boolean) {
        if (_state.value.writesBlocked) return
        val previousValue = _state.value.priceDropEnabled
        _state.update { it.copy(priceDropEnabled = enabled, isToggling = true, error = null) }

        scope.launch {
            notificationsService.togglePriceDrop(enabled).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            priceDropEnabled = enabled,
                            isToggling = false,
                            error = null
                        )
                    }
                },
                onError = { networkError ->
                    _state.update {
                        it.copy(
                            priceDropEnabled = previousValue,
                            isToggling = false,
                            error = NotificationPrefsError.Network(networkError)
                        )
                    }
                }
            )
        }
    }
}
