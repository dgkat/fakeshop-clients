package org.example.fakeshop_clients.core.error_handling

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ApiErrorEnvelope(
    val code: String,
    val message: String? = null
)

private val envelopeJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun NetworkError.HttpError.parseEnvelope(): ApiErrorEnvelope? {
    val raw = body ?: return null
    return try {
        envelopeJson.decodeFromString(ApiErrorEnvelope.serializer(), raw)
    } catch (_: Exception) {
        null
    }
}
