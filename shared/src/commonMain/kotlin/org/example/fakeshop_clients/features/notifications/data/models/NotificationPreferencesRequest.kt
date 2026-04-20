package org.example.fakeshop_clients.features.notifications.data.models

import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferencesRequest(val priceDropEnabled: Boolean)
