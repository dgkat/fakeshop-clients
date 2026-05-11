package org.example.fakeshop_clients.core.auth.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Role { GUEST, LOGGED_USER, ADMIN }

val Role.isLoggedIn: Boolean get() = this != Role.GUEST

sealed interface SessionState {
    data object Unknown : SessionState          // cold start, pre-bootstrap
    data object BootstrapFailed : SessionState  // /auth/guest failed — hard error screen
    data class Authenticated(val role: Role) : SessionState
}

interface SessionObserver {
    val state: StateFlow<SessionState>
    val upgradeInProgress: StateFlow<Boolean>
}

interface SessionMutator {
    fun setAuthenticated(role: Role)
    fun setBootstrapFailed()
    fun setUpgradeInProgress(inProgress: Boolean)
}

class SessionStore : SessionObserver, SessionMutator {
    private val _state = MutableStateFlow<SessionState>(SessionState.Unknown)
    override val state: StateFlow<SessionState> = _state.asStateFlow()

    private val _upgradeInProgress = MutableStateFlow(false)
    override val upgradeInProgress: StateFlow<Boolean> = _upgradeInProgress.asStateFlow()

    override fun setAuthenticated(role: Role) {
        _state.value = SessionState.Authenticated(role)
    }

    override fun setBootstrapFailed() {
        _state.value = SessionState.BootstrapFailed
    }

    override fun setUpgradeInProgress(inProgress: Boolean) {
        _upgradeInProgress.value = inProgress
    }
}
