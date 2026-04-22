package org.example.fakeshop_clients.core.auth.domain

interface RoleResolver {
    // Returns the current session role, or null when unauthenticated.
    suspend fun currentRole(): Role?
}
