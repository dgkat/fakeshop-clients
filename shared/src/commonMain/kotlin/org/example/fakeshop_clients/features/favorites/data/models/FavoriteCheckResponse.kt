package org.example.fakeshop_clients.features.favorites.data.models

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteCheckResponse(val isFavorited: Boolean)
