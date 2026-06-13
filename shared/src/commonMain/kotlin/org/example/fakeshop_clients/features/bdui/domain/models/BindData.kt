package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Opaque holder for a resolved BDUI action-context [JsonObject], and the universal currency for
 * dispatching a BDUI action across every platform (see `ProductDetailViewStore.dispatchBduiAction`).
 *
 * It exists so the context is never handed across the SKIE bridge as a `JsonObject`: a `JsonObject`
 * IS-A `Map`, so SKIE auto-bridges it to a Swift `Dictionary` and back, and round-tripping it
 * (build in Kotlin → Swift `[String: JsonElement]` → back into Kotlin) yields a bridged map whose
 * backing can be freed, crashing later on `Map#get`. Wrapped in a plain class, SKIE passes it by
 * reference and the [json] is only ever unwrapped Kotlin-side. Android has no bridge to worry about
 * but uses the same wrapper so the dispatch entry point stays single and cross-platform.
 */
class ActionContext(val json: JsonObject)

/**
 * Swift-friendly wrapper around the merged bind data JsonObject.
 * SwiftUI renderers call these methods directly instead of touching JsonObject.
 */
class BindData(val json: JsonObject) {
    fun resolveString(path: String): String? = json.resolveString(path)
    fun resolveDouble(path: String): Double? = json.resolveDouble(path)
    fun resolveStringList(path: String): List<String>? = json.resolveStringList(path)
    fun resolveSpecPairs(path: String): List<Pair<String, String>>? = json.resolveSpecPairs(path)
    fun resolveSpecItems(path: String): List<SpecItem>? = json.resolveSpecItems(path)
    fun resolveColorList(path: String): List<ColorEntry>? = json.resolveColorList(path)

    /**
     * Builds a JsonObject for a BDUI action context.
     * Resolves each binding path against this BindData, then merges any extra
     * literal key/value pairs (e.g. the selected size or color from a tap event).
     */
    fun resolveActionContext(bindings: Map<String, String>, extra: Map<String, String> = emptyMap()): ActionContext =
        ActionContext(
            buildJsonObject {
                bindings.forEach { (key, path) -> json.resolve(path)?.let { put(key, it) } }
                extra.forEach { (k, v) -> put(k, v) }
            }
        )
}
