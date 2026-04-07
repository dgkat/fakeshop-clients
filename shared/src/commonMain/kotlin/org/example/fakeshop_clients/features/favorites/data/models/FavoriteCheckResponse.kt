package org.example.fakeshop_clients.features.favorites.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteCheckResponse(@SerialName("liked") val isFavorited: Boolean)
