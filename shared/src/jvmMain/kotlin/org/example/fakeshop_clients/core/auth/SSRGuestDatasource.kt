package org.example.fakeshop_clients.core.auth

import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.client.request.post
import kotlin.coroutines.cancellation.CancellationException
import org.slf4j.LoggerFactory

class SSRGuestDatasource(private val httpClient: HttpClient) {

    private val logger = LoggerFactory.getLogger(SSRGuestDatasource::class.java)

    // Returns all Set-Cookie headers from a successful /api/web/auth/guest response, or null on failure.
    suspend fun createGuestSession(): List<String>? {
        return try {
            val response: HttpResponse = httpClient.post("/api/web/auth/guest")
            if (response.status == HttpStatusCode.OK) {
                val cookies = response.headers.getAll(HttpHeaders.SetCookie) ?: emptyList()
                if (cookies.isEmpty()) {
                    logger.error("POST /api/web/auth/guest returned 200 but no Set-Cookie headers")
                }
                cookies
            } else {
                logger.error("POST /api/web/auth/guest failed with status ${response.status.value}")
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.error("POST /api/web/auth/guest threw an exception: ${e::class.simpleName}: ${e.message}")
            null
        }
    }

    companion object {
        // Must match the session cookie name set by the backend on POST /api/web/auth/guest.
        const val SESSION_COOKIE_NAME = "SESSION"
    }
}
