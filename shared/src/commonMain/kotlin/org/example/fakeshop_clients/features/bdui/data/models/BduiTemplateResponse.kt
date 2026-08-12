package org.example.fakeshop_clients.features.bdui.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Wire model. `root` stays as raw JSON because the project's platform `ApiClient`
 * implementations use their own `Json` config without `classDiscriminator = "type"`.
 * The mapper re-parses `root` with `BduiJson` to a typed `UiNode`.
 */
@Serializable
data class BduiTemplateResponse(
    val schemaVersion: Int,
    val screen: String,
    val category: String,
    val root: JsonObject
)
