package org.example.fakeshop_clients.core.auth.domain

import org.example.fakeshop_clients.core.error_handling.fold

class SessionBootstrapper(
    private val roleResolver: RoleResolver,
    private val authRepository: AuthRepository,
    private val installIdProvider: InstallIdProvider,
    private val sessionMutator: SessionMutator,
    private val sessionObserver: SessionObserver
) {
    suspend fun bootstrap() {
        if (sessionObserver.state.value is SessionState.Authenticated) return

        val role = roleResolver.currentRole()
        if (role != null) {
            sessionMutator.setAuthenticated(role)
            return
        }

        val installId = installIdProvider.get()
        authRepository.guest(installId).fold(
            onSuccess = { sessionMutator.setAuthenticated(Role.GUEST) },
            onError = { sessionMutator.setBootstrapFailed() }
        )
    }
}
