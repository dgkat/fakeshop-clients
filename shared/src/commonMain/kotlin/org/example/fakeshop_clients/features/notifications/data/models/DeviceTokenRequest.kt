package org.example.fakeshop_clients.features.notifications.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenRequest(val token: String, val platform: String)
