package org.example.fakeshop_clients.features.favorites.data.models

import kotlinx.serialization.Serializable

@Serializable
data class BulkFavoriteCheckResponse(val favoritedProductIds: List<String>)
