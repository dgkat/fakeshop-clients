package org.example.fakeshop_clients.core.interactions.data

import kotlinx.browser.document

internal object BrowserCookies {

    fun read(name: String): String? {
        val prefix = "$name="
        return document.cookie
            .split(';')
            .map { it.trim() }
            .firstOrNull { it.startsWith(prefix) }
            ?.substring(prefix.length)
            ?.takeIf { it.isNotBlank() }
            ?.let { decodeURIComponent(it) }
    }

    // Not HttpOnly by design: the Axios path needs JS to read it. This is an opaque analytics
    // grouping key, never an auth token.
    fun write(name: String, value: String, maxAgeSeconds: Int) {
        document.cookie =
            "$name=${encodeURIComponent(value)}; Path=/; Max-Age=$maxAgeSeconds; SameSite=Lax"
    }

    fun remove(name: String) {
        document.cookie = "$name=; Path=/; Max-Age=0; SameSite=Lax"
    }
}

private fun encodeURIComponent(value: String): String =
    js("encodeURIComponent")(value).unsafeCast<String>()

private fun decodeURIComponent(value: String): String =
    js("decodeURIComponent")(value).unsafeCast<String>()
