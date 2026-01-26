package org.example.fakeshop_clients.features.home.data.models

import kotlinx.serialization.Serializable

@Serializable
data class BriefProductsResponse(
    val briefProducts: List<BriefProductResponse>
)

