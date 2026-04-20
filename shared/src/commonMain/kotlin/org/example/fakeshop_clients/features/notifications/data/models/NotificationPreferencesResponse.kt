package org.example.fakeshop_clients.features.notifications.data.models

import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferencesResponse(val priceDropEnabled: Boolean)
