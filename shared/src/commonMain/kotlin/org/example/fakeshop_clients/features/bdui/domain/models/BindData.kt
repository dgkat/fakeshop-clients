package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.json.JsonObject

/**
 * Swift-friendly wrapper around the merged bind data JsonObject.
 * SwiftUI renderers call these methods directly instead of touching JsonObject.
 */
class BindData(val json: JsonObject) {
    fun resolveString(path: String): String? = json.resolveString(path)
    fun resolveDouble(path: String): Double? = json.resolveDouble(path)
    fun resolveStringList(path: String): List<String>? = json.resolveStringList(path)
    fun resolveSpecPairs(path: String): List<Pair<String, String>>? = json.resolveSpecPairs(path)
    fun resolveColorList(path: String): List<ColorEntry>? = json.resolveColorList(path)
}
