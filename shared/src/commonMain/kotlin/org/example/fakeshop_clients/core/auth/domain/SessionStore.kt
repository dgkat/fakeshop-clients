package org.example.fakeshop_clients.core.auth.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface SessionState {
    data object Unknown : SessionState
    data object LoggedOut : SessionState
    data object LoggedIn : SessionState
}

interface SessionObserver {
    val state: StateFlow<SessionState>
}

interface SessionMutator {
    fun setLoggedIn()
    fun setLoggedOut()
}

class SessionStore : SessionObserver, SessionMutator {
    private val _state = MutableStateFlow<SessionState>(SessionState.Unknown)
    override val state: StateFlow<SessionState> = _state.asStateFlow()

    override fun setLoggedIn() {
        _state.value = SessionState.LoggedIn
    }

    override fun setLoggedOut() {
        _state.value = SessionState.LoggedOut
    }
}
