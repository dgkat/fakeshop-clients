package org.example.fakeshop_clients.core.assets

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object AssetManifest {
    val islandsBundle: String = "/static/js/${resolve("islands-manifest.json", "islands-bundle.js")}"
    val spaBundle: String = "/static/js/${resolve("spa-manifest.json", "spa-bundle.js")}"

    private val cssManifest: Map<String, String> by lazy {
        try {
            val text = AssetManifest::class.java.classLoader
                .getResourceAsStream("static/css/bundles/css-manifest.json")
                ?.bufferedReader()?.readText() ?: return@lazy emptyMap()
            Json.parseToJsonElement(text).jsonObject.mapValues { it.value.jsonPrimitive.content }
        } catch (_: Exception) { emptyMap() }
    }

    val commonCss: String get() = "/static/css/bundles/${cssManifest["common"] ?: "common.css"}"
    val homeCss: String get() = "/static/css/bundles/${cssManifest["home"] ?: "home.css"}"
    val productDetailCss: String get() = "/static/css/bundles/${cssManifest["product-detail"] ?: "product-detail.css"}"
    val spaCss: String get() = "/static/css/bundles/${cssManifest["spa"] ?: "spa.css"}"

    private fun resolve(manifestFile: String, fallback: String): String = try {
        val text = AssetManifest::class.java.classLoader
            .getResourceAsStream("static/js/$manifestFile")
            ?.bufferedReader()?.readText() ?: return fallback
        Json.parseToJsonElement(text).jsonObject.values.first().jsonPrimitive.content
    } catch (_: Exception) { fallback }
}
