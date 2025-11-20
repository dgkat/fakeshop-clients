package org.example.fakeshop_clients.core.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class TokenRefreshResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null
)
//TODO make these non nullable when impl different response for web / mobile