package org.example.fakeshop_clients.core.i18n

import kotlinx.browser.window

object I18n {
    private val strings: Map<String, String> by lazy {
        val jsStrings = js("window.__STRINGS__")
        if (jsStrings != null && jsStrings != undefined) {
            val result = mutableMapOf<String, String>()
            val keys = js("Object.keys(window.__STRINGS__)") as Array<String>
            keys.forEach { key ->
                val value = jsStrings[key]
                if (value != null && value != undefined) {
                    result[key] = value.toString()
                }
            }
            result
        } else {
            emptyMap()
        }
    }

    val locale: String by lazy {
        val jsLocale = js("window.__LOCALE__")
        if (jsLocale != null && jsLocale != undefined) {
            jsLocale.toString()
        } else {
            // Fallback: extract from URL path
            val path = window.location.pathname
            val segments = path.split("/").filter { it.isNotEmpty() }
            val firstSegment = segments.firstOrNull() ?: "en"
            if (firstSegment in setOf("en", "es")) firstSegment else "en"
        }
    }

    fun getString(key: String): String {
        return strings[key] ?: key
    }
}

fun getString(key: String): String = I18n.getString(key)
