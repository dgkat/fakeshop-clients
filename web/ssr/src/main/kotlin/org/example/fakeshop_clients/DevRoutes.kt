package org.example.fakeshop_clients

import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.ByteArrayContent
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.handle
import io.ktor.server.routing.route
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

// Proxies /api/* to the backend for local development.
// In production the reverse proxy intercepts /api/ before reaching Ktor, so this route is never registered there.
fun Route.devApiProxy() {
    route("/api/{...}") {
        handle {
            val client by inject<HttpClient>(named("guestHttpClient"))
            val requestUri = call.request.uri
            val requestMethod = call.request.httpMethod
            val requestBody = runCatching { call.receive<ByteArray>() }.getOrDefault(byteArrayOf())
            val contentType = call.request.headers[HttpHeaders.ContentType]

            val backendResponse = client.request(requestUri) {
                method = requestMethod
                headers {
                    call.request.headers.forEach { name, values ->
                        if (name !in setOf(HttpHeaders.Host, HttpHeaders.ContentLength, HttpHeaders.TransferEncoding)) {
                            values.forEach { append(name, it) }
                        }
                    }
                }
                if (requestBody.isNotEmpty()) {
                    val ct = contentType?.let { ContentType.parse(it) }
                        ?: ContentType.Application.Json
                    setBody(ByteArrayContent(requestBody, ct))
                }
            }

            val responseBytes = backendResponse.bodyAsBytes()
            backendResponse.headers.forEach { name, values ->
                if (name !in setOf(HttpHeaders.TransferEncoding, HttpHeaders.ContentLength)) {
                    values.forEach { call.response.headers.append(name, it) }
                }
            }
            call.respond(backendResponse.status, ByteArrayContent(responseBytes))
        }
    }
}
