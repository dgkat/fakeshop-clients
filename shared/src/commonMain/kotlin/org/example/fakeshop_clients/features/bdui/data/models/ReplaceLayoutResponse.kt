package org.example.fakeshop_clients.features.bdui.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Wire model for a ReplaceLayout (the reusable UI subtree authored in the CMS).
 *
 * `node` stays as raw JSON because the project's platform `ApiClient` implementations
 * use their own `Json` config without `classDiscriminator = "type"`. The mapper re-parses
 * `node` with `BduiJson` to a typed `UiNode` — mirrors [BduiTemplateResponse].
 */
@Serializable
data class ReplaceLayoutResponse(
    val id: String,
    val name: String,
    val defaultTargetSlotId: String? = null,
    val node: JsonObject,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
