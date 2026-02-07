package org.example.fakeshop_clients.core.extensions

import io.ktor.server.application.ApplicationCall
import org.example.fakeshop_clients.features.core.models.Cookies

/**
 * Extracts all cookies from the incoming request and wraps them in a Cookies object.
 * Used to forward user cookies to backend API for authenticated SSR requests.
 */
fun ApplicationCall.extractCookies(): Cookies {
    val cookiesMap = mutableMapOf<String, String>()
    request.cookies.rawCookies.forEach { (name, _) ->
        request.cookies[name]?.let { value ->
            cookiesMap[name] = value
        }
    }
    return Cookies(cookiesMap)
}