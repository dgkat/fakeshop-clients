package org.example.fakeshop_clients.core.data.fetchClient

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import kotlin.js.json
import kotlin.reflect.KClass

class PublicFetchClient(private val baseUrl: String) : PublicApiClient {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KClass<T>
    ): T {
        @Suppress("UNCHECKED_CAST")
        val bodySerializer = body::class.serializer() as SerializationStrategy<B>
        val bodyJson = jsonParser.encodeToString(bodySerializer, body)

        val response = window.fetch(
            "$baseUrl$path", RequestInit(
                method = "POST",
                headers = json("Content-Type" to "application/json"),
                body = bodyJson,
                credentials = RequestCredentials.INCLUDE
            )
        ).await()

        if (!response.ok) {
            throw Exception("HTTP error! status: ${response.status}")
        }

        val responseText = response.text().await()
        return jsonParser.decodeFromString(responseType.serializer(), responseText)
    }
}