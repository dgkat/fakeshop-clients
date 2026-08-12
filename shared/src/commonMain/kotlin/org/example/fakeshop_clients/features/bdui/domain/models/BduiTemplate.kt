package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class BduiTemplate(
    val schemaVersion: Int,
    val screen: String,
    val category: String,
    val root: UiNode
)
