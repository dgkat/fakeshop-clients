package org.example.fakeshop_clients.core.auth.domain

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val lenientJson = Json { ignoreUnknownKeys = true }

// Decodes the JWT payload segment and returns the `role` claim as a Role, or null if absent/unrecognised.
// We only base64-decode the middle segment — no signature verification needed client-side.
@OptIn(ExperimentalEncodingApi::class)
fun parseRoleFromJwt(token: String): Role? {
    return try {
        val payload = token.split('.').getOrNull(1) ?: return null
        val decoded = Base64.UrlSafe
            .withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
            .decode(payload)
            .decodeToString()
        val roleStr = lenientJson.parseToJsonElement(decoded)
            .jsonObject["role"]
            ?.jsonPrimitive
            ?.content
            ?: return null
        Role.entries.find { it.name == roleStr }
    } catch (_: Exception) {
        null
    }
}
