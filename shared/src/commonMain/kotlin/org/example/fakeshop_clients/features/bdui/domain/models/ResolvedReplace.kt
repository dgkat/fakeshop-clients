package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.json.JsonObject

/**
 * A fully resolved replacement, ready for the renderer to swap in: the layout's UI
 * subtree [node] bound against its own standalone [values] namespace (never merged
 * into the product `bindData`).
 */
data class ResolvedReplace(
    val node: UiNode,
    val values: JsonObject
)
