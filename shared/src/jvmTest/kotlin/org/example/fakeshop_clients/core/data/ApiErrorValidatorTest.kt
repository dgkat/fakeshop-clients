package org.example.fakeshop_clients.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.parseEnvelope
import org.example.fakeshop_clients.features.core.models.Cookies
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Regression coverage for item 6 (HTTP error bodies discarded). Drives the real SSR HTTP stack —
 * `expectSuccess = true` + [installApiErrorValidator] + [KtorNetworkExceptionMapper] via
 * [SSRSafeApiClient] over a [MockEngine] — and asserts the server error envelope now reaches the UI
 * layer as a fully-populated [NetworkError.HttpError] (previously `body = null`).
 */
class ApiErrorValidatorTest {

    @Serializable
    private data class Dummy(val id: String)

    private val noCookies = Cookies(emptyMap())

    private fun clientReturning(
        status: HttpStatusCode,
        body: String,
        contentType: String = "application/json"
    ): SSRSafeApiClient {
        val engine = MockEngine {
            respond(
                content = ByteReadChannel(body),
                status = status,
                headers = headersOf(HttpHeaders.ContentType, contentType)
            )
        }
        val httpClient = HttpClient(engine) {
            expectSuccess = true
            installApiErrorValidator()
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
        return SSRSafeApiClient(httpClient, KtorNetworkExceptionMapper())
    }

    @Test
    fun jsonErrorEnvelopeReachesTheUiAsHttpErrorWithBodyAndMessage() = runTest {
        val envelopeBody = """{"code":"PRODUCT_NOT_FOUND","message":"No such product"}"""
        val client = clientReturning(HttpStatusCode.NotFound, envelopeBody)

        val result = client.get<Dummy>("/product/x", noCookies)

        val error = assertIs<Result.Error<NetworkError>>(result).error
        val httpError = assertIs<NetworkError.HttpError>(error)
        assertEquals(404, httpError.code)
        // message is lifted from the envelope, not just the status reason phrase
        assertEquals("No such product", httpError.message)
        // body is no longer discarded — the raw envelope survives to the presentation layer
        assertEquals(envelopeBody, httpError.body)

        val envelope = assertNotNull(httpError.parseEnvelope())
        assertEquals("PRODUCT_NOT_FOUND", envelope.code)
    }

    @Test
    fun nonEnvelopeErrorBodyStillSurfacesCodeAndBodyWithReasonPhraseMessage() = runTest {
        val client = clientReturning(
            status = HttpStatusCode.InternalServerError,
            body = "upstream exploded",
            contentType = "text/plain"
        )

        val result = client.get<Dummy>("/product/x", noCookies)

        val httpError = assertIs<NetworkError.HttpError>(assertIs<Result.Error<NetworkError>>(result).error)
        assertEquals(500, httpError.code)
        assertEquals("upstream exploded", httpError.body)
        // no decodable envelope → message falls back to the HTTP status reason phrase
        assertEquals(HttpStatusCode.InternalServerError.description, httpError.message)
        assertNull(httpError.parseEnvelope())
    }

    @Test
    fun successfulResponseIsUnaffectedByTheValidator() = runTest {
        val client = clientReturning(HttpStatusCode.OK, """{"id":"abc"}""")

        val result = client.get<Dummy>("/product/abc", noCookies)

        val success = assertIs<Result.Success<Dummy>>(result)
        assertEquals("abc", success.data.id)
    }
}
