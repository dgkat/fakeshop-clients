package org.example.fakeshop_clients.core.assets

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object AssetManifest {
    val islandsBundle: String = "/static/js/${resolve("islands-manifest.json", "islands-bundle.js")}"
    val spaBundle: String = "/static/js/${resolve("spa-manifest.json", "spa-bundle.js")}"

    private fun resolve(manifestFile: String, fallback: String): String = try {
        val text = AssetManifest::class.java.classLoader
            .getResourceAsStream("static/js/$manifestFile")
            ?.bufferedReader()?.readText() ?: return fallback
        Json.parseToJsonElement(text).jsonObject.values.first().jsonPrimitive.content
    } catch (_: Exception) { fallback }
}
