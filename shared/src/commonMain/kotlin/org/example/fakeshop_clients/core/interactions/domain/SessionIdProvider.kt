package org.example.fakeshop_clients.core.interactions.domain

interface SessionIdProvider {
    suspend fun current(): String
    suspend fun reset()
}
