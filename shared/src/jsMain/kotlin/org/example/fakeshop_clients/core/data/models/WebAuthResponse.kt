package org.example.fakeshop_clients.core.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WebAuthResponse(
    val success: Boolean
)
