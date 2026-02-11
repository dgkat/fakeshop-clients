package org.example.fakeshop_clients.core.i18n

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

object WebStrings {
    val SUPPORTED_LOCALES = setOf("en", "es")
    const val DEFAULT_LOCALE = "en"

    private val stringsMap: Map<String, Map<String, String>> by lazy {
        SUPPORTED_LOCALES.associateWith { locale -> loadStrings(locale) }
    }

    private fun loadStrings(locale: String): Map<String, String> {
        val resource = this::class.java.classLoader.getResource("strings/$locale.json")
            ?: error("String resource not found for locale: $locale")
        val jsonText = resource.readText()
        val jsonObject = Json.parseToJsonElement(jsonText) as JsonObject
        return jsonObject.mapValues { (_, value) -> value.jsonPrimitive.content }
    }

    fun get(locale: String, key: String): String {
        val safeLocale = if (locale in SUPPORTED_LOCALES) locale else DEFAULT_LOCALE
        return stringsMap[safeLocale]?.get(key)
            ?: stringsMap[DEFAULT_LOCALE]?.get(key)
            ?: key
    }

    fun getAll(locale: String): Map<String, String> {
        val safeLocale = if (locale in SUPPORTED_LOCALES) locale else DEFAULT_LOCALE
        return stringsMap[safeLocale] ?: stringsMap[DEFAULT_LOCALE] ?: emptyMap()
    }

    fun getAllAsJson(locale: String): String {
        val strings = getAll(locale)
        return Json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(),
            JsonObject(strings.mapValues { (_, v) ->
                kotlinx.serialization.json.JsonPrimitive(v)
            })
        )
    }

    fun parseAcceptLanguage(header: String?): String {
        if (header.isNullOrBlank()) return DEFAULT_LOCALE
        return header.split(",")
            .map { part ->
                val segments = part.trim().split(";")
                val lang = segments[0].trim().split("-")[0].lowercase()
                val quality = segments.getOrNull(1)
                    ?.trim()
                    ?.removePrefix("q=")
                    ?.toDoubleOrNull()
                    ?: 1.0
                lang to quality
            }
            .sortedByDescending { it.second }
            .firstOrNull { it.first in SUPPORTED_LOCALES }
            ?.first
            ?: DEFAULT_LOCALE
    }
}
