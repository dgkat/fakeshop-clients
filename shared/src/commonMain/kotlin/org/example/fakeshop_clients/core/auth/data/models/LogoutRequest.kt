package org.example.fakeshop_clients.core.auth.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(val refreshToken: String)