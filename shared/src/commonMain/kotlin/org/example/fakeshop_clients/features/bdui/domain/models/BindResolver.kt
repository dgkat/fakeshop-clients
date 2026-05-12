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

/**
 * Specs are objects with arbitrary keys → primitive values.
 * Returns ordered (key, value) pairs to preserve template-defined order.
 */
fun JsonObject.resolveSpecPairs(path: String): List<Pair<String, String>>? {
    val element = resolve(path) ?: return null
    if (element !is JsonObject) return null
    return element.entries.map { (k, v) ->
        k to ((v as? JsonPrimitive)?.content ?: v.toString())
    }
}

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
