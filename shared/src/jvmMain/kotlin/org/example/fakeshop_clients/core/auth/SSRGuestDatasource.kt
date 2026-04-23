package org.example.fakeshop_clients.core.auth

import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.client.request.post

class SSRGuestDatasource(private val httpClient: HttpClient) {

    // Returns all Set-Cookie headers from a successful /api/web/auth/guest response, or null on failure.
    suspend fun createGuestSession(): List<String>? {
        return try {
            val response: HttpResponse = httpClient.post("/api/web/auth/guest")
            if (response.status == HttpStatusCode.OK) {
                response.headers.getAll(HttpHeaders.SetCookie) ?: emptyList()
            } else null
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        // Must match the session cookie name set by the backend on POST /api/web/auth/guest.
        const val SESSION_COOKIE_NAME = "sid"
    }
}
