package org.example.fakeshop_clients.core.data

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import org.example.fakeshop_clients.core.error_handling.ApiException
import org.example.fakeshop_clients.core.error_handling.decodeApiErrorEnvelope
import kotlin.coroutines.cancellation.CancellationException

/** Cap the error body we buffer so a huge/hostile error page can't blow up memory. */
private const val MAX_ERROR_BODY_CHARS = 8 * 1024

fun HttpClientConfig<*>.installApiErrorValidator() {
    HttpResponseValidator {
        handleResponseExceptionWithRequest { cause, _ ->
            val responseException = cause as? ResponseException ?: return@handleResponseExceptionWithRequest
            val response = responseException.response

            val rawBody = try {
                response.bodyAsText().take(MAX_ERROR_BODY_CHARS)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                null
            }

            throw ApiException(
                status = response.status.value,
                rawBody = rawBody,
                envelope = decodeApiErrorEnvelope(rawBody),
                statusDescription = response.status.description
            )
        }
    }
}
