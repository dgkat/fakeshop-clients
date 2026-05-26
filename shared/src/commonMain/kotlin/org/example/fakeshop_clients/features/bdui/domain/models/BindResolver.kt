package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun JsonObject.resolve(path: String): JsonElement? {
    val tokens = path.split(".")
    var current: JsonElement = this
    for (token in tokens) {
        current = when (current) {
            is JsonObject -> current[token] ?: return null
            is JsonArray -> {
                val index = token.toIntOrNull() ?: return null
                current.getOrNull(index) ?: return null
            }
            else -> return null
        }
    }
    return current
}

fun JsonObject.resolveString(path: String): String? =
    (resolve(path) as? JsonPrimitive)?.takeIf { it.isString }?.content
        ?: (resolve(path) as? JsonPrimitive)?.content

fun JsonObject.resolveDouble(path: String): Double? =
    (resolve(path) as? JsonPrimitive)?.doubleOrNull

fun JsonObject.resolveStringList(path: String): List<String>? {
    val element = resolve(path) ?: return null
    if (element !is JsonArray) return null
    return element.map { (it as? JsonPrimitive)?.content ?: return null }
}

sealed class SpecItem {
    data class Group(val title: String) : SpecItem()
    data class Row(val label: String, val value: String) : SpecItem()
}

/**
 * Resolves a spec array into a flat list of [SpecItem]s.
 * Each array element may carry an optional "group" (emitted as a [SpecItem.Group] header
 * before the row) plus required "label"/"value" fields (emitted as a [SpecItem.Row]).
 */
fun JsonObject.resolveSpecItems(path: String): List<SpecItem>? {
    val element = resolve(path) ?: return null
    if (element !is JsonArray) return null
    val result = mutableListOf<SpecItem>()
    for (item in element) {
        val obj = item as? JsonObject ?: continue
        val group = (obj["group"] as? JsonPrimitive)?.content
        val label = (obj["label"] as? JsonPrimitive)?.content
        val value = (obj["value"] as? JsonPrimitive)?.content
        if (group != null) result.add(SpecItem.Group(group))
        if (label != null && value != null) result.add(SpecItem.Row(label, value))
    }
    return result.takeIf { it.isNotEmpty() }
}

/** Legacy flat-object variant kept for callers that don't need group headers. */
fun JsonObject.resolveSpecPairs(path: String): List<Pair<String, String>>? =
    resolveSpecItems(path)?.filterIsInstance<SpecItem.Row>()?.map { it.label to it.value }?.takeIf { it.isNotEmpty() }

/**
 * Color list is an array of objects with `name` and `hex` keys.
 */
data class ColorEntry(val name: String, val hex: String)

fun JsonObject.resolveColorList(path: String): List<ColorEntry>? {
    val element = resolve(path) ?: return null
    if (element !is JsonArray) return null
    return element.map { item ->
        val obj = (item as? JsonObject) ?: return null
        val name = (obj["name"] as? JsonPrimitive)?.content ?: return null
        val hex = (obj["hex"] as? JsonPrimitive)?.content ?: return null
        ColorEntry(name, hex)
    }
}
