package org.example.fakeshop_clients.features.home.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestResponse(
    val data: Data,
    val id: String,
    val name: String
)

@Serializable
data class Data(
    @SerialName("CPU model")
    val cpuModel: String,
    @SerialName("Hard disk size")
    val diskSize: String,
    val price: Double,
    val year: Int
)