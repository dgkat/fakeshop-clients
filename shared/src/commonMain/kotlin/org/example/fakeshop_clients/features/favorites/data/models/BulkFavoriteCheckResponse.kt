package org.example.fakeshop_clients.features.favorites.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BulkFavoriteCheckResponse(@SerialName("likedProductIds") val favoritedProductIds: List<String>)
