package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    /**
     * Builds a JsonObject for a BDUI action context.
     * Resolves each binding path against this BindData, then merges any extra
     * literal key/value pairs (e.g. the selected size or color from a tap event).
     */
    fun resolveActionContext(bindings: Map<String, String>, extra: Map<String, String> = emptyMap()): JsonObject =
        buildJsonObject {
            bindings.forEach { (key, path) -> json.resolve(path)?.let { put(key, it) } }
            extra.forEach { (k, v) -> put(k, v) }
        }
}
