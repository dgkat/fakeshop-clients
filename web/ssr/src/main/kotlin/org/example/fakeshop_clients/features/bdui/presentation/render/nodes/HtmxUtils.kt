package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.serialization.json.Json.Default.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString

internal fun buildResolvedContext(bindings: Map<String, String>, data: JsonObject): Map<String, String> =
    bindings.mapNotNull { (key, path) -> data.resolveString(path)?.let { key to it } }.toMap()

internal fun buildHxVals(
    actionId: String,
    screen: String,
    extra: Map<String, String> = emptyMap()
): String {
    val obj = buildJsonObject {
        put("actionId", actionId)
        put("screen", screen)
        put("schemaVersion", BduiConstants.CURRENT_SCHEMA_VERSION)
        extra.forEach { (k, v) -> put(k, v) }
    }
    return encodeToString(JsonObject.serializer(), obj)
}
