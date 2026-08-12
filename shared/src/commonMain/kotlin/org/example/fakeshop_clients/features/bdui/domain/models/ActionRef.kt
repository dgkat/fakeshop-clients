package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ActionRef(
    val type: String,
    val target: String? = null
)
