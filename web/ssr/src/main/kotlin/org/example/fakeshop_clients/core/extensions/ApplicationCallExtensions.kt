package org.example.fakeshop_clients.core.extensions

import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import org.example.fakeshop_clients.core.auth.SSRGuestDatasource
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

/**
 * Ensures the browser has a valid session cookie. If the session cookie is absent, creates
 * a guest session via the backend and forwards the resulting Set-Cookie to the browser.
 *
 * Returns updated Cookies (including the new session cookie) for use in subsequent
 * backend calls within this request, or null if guest creation failed.
 */
suspend fun ApplicationCall.ensureGuestSession(datasource: SSRGuestDatasource): Cookies? {
    val cookies = extractCookies()
    if (cookies.data.containsKey(SSRGuestDatasource.SESSION_COOKIE_NAME)) {
        return cookies
    }

    val setCookieHeaders = datasource.createGuestSession() ?: return null

    // Forward all Set-Cookie headers to the browser so subsequent requests carry the session.
    setCookieHeaders.forEach { header ->
        response.headers.append(HttpHeaders.SetCookie, header)
    }

    // Extract the session cookie value for use in this request's backend calls.
    val sessionHeader = setCookieHeaders
        .firstOrNull { it.startsWith("${SSRGuestDatasource.SESSION_COOKIE_NAME}=") }
        ?: return null

    val cookieValue = sessionHeader
        .substringAfter("${SSRGuestDatasource.SESSION_COOKIE_NAME}=")
        .substringBefore(";")
        .trim()

    return Cookies(cookies.data + mapOf(SSRGuestDatasource.SESSION_COOKIE_NAME to cookieValue))
}