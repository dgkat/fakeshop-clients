package org.example.fakeshop_clients.core.auth.domain

interface InstallIdProvider {
    // Returns a stable ID for this install. Generated on first call and persisted.
    // Deliberately does NOT survive a reinstall — that signals a new guest identity.
    suspend fun get(): String
}
