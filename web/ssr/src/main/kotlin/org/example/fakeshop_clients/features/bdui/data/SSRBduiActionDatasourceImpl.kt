package org.example.fakeshop_clients.features.bdui.data

import io.ktor.client.request.cookie
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.fakeshop_clients.core.data.SSRSafeApiClient
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.bdui.BduiJson
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionRequest
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionResponse
import org.example.fakeshop_clients.features.core.models.Cookies

// Uses BduiJson for polymorphic BduiMutation deserialization (classDiscriminator = "type"),
// bypassing the shared HttpClient's ContentNegotiation which uses a plain Json instance.
class SSRBduiActionDatasourceImpl(
    private val safeApiClient: SSRSafeApiClient,
    private val baseUrl: UrlProvider
) : SSRBduiActionDatasource {

    override suspend fun postAction(
        request: BduiActionRequest,
        cookies: Cookies
    ): Result<BduiActionResponse, NetworkError> {
        return try {
            val response = safeApiClient.httpClient.post("${baseUrl()}/ui/action") {
                contentType(ContentType.Application.Json)
                setBody(BduiJson.encodeToString(BduiActionRequest.serializer(), request))
                cookies.data.forEach { (name, value) -> cookie(name, value) }
            }
            val body = response.bodyAsText()
            Result.Success(BduiJson.decodeFromString<BduiActionResponse>(body))
        } catch (e: Exception) {
            Result.Error(safeApiClient.exceptionMapper.map(e))
        }
    }
}
