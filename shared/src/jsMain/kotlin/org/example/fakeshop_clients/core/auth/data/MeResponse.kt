package org.example.fakeshop_clients.core.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class MeResponse(
    val role: String? = null
)
