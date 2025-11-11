package org.example.fakeshop_clients.features.home.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RemoteBriefProduct(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: String
)