package org.example.fakeshop_clients.core.data.axios

import kotlinx.coroutines.await
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.example.fakeshop_clients.core.data.ApiClient
import kotlin.reflect.KClass

class PublicAxiosCLient(private val baseUrl: String) : ApiClient {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun <T : Any> get(path: String, responseType: KClass<T>): T {
        val response = axios.get("$baseUrl$path").await()
        return parseResponse(response.data, responseType)
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
        val bodyObject = JSON.parse<dynamic>(bodyJson)

        val response = axios.post("$baseUrl$path", bodyObject).await()
        return parseResponse(response.data, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any, B : Any> put(
        path: String,
        body: B,
        responseType: KClass<T>
    ): T {
        @Suppress("UNCHECKED_CAST")
        val bodySerializer = body::class.serializer() as SerializationStrategy<B>
        val bodyJson = jsonParser.encodeToString(bodySerializer, body)
        val bodyObject = JSON.parse<dynamic>(bodyJson)

        val response = axios.put("$baseUrl$path", bodyObject).await()
        return parseResponse(response.data, responseType)
    }

    override suspend fun <T : Any> delete(path: String, responseType: KClass<T>): T {
        val response = axios.delete("$baseUrl$path").await()
        return parseResponse(response.data, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    private fun <T : Any> parseResponse(data: dynamic, responseType: KClass<T>): T {
        val jsonString = JSON.stringify(data)
        return jsonParser.decodeFromString(responseType.serializer(), jsonString)
    }
}