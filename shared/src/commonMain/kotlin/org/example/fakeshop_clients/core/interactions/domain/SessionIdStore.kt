package org.example.fakeshop_clients.core.interactions.domain

data class StoredSession(
    val id: String,
    val lastTouchedMillis: Long?
)

interface SessionIdStore {
    suspend fun read(): StoredSession?
    suspend fun write(id: String, lastTouchedMillis: Long)
    suspend fun clear()
}

fun interface SessionIdGenerator {
    fun newId(): String
}
