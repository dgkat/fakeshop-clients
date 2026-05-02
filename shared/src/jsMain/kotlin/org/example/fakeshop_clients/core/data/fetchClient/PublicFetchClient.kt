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

        return fetchPost(path, bodyJson, emptyMap(), responseType)
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any, B : Any> postWithHeaders(
        path: String,
        body: B,
        headers: Map<String, String>,
        responseType: KClass<T>
    ): T {
        @Suppress("UNCHECKED_CAST")
        val bodySerializer = body::class.serializer() as SerializationStrategy<B>
        val bodyJson = jsonParser.encodeToString(bodySerializer, body)
        return fetchPost(path, bodyJson, headers, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    private suspend fun <T : Any> fetchPost(
        path: String,
        bodyJson: String,
        extraHeaders: Map<String, String>,
        responseType: KClass<T>
    ): T {
        val headersObj = json("Content-Type" to "application/json").apply {
            extraHeaders.forEach { (k, v) -> asDynamic()[k] = v }
        }
        val response = window.fetch(
            "$baseUrl$path", RequestInit(
                method = "POST",
                headers = headersObj,
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