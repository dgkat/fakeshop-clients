package org.example.fakeshop_clients.core.api_client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo
import org.example.fakeshop_clients.core.data.ApiClient
import kotlin.reflect.KClass

/**
 * JVM implementation of ApiClient using Ktor HttpClient.
 * Used by SSR server to make HTTP calls to the backend API.
 */
class KtorClient(val http: HttpClient) : ApiClient {

    override suspend fun <T : Any> get(path: String, responseType: KClass<T>): T {
        val response = http.get(path)
        return response.body(TypeInfo(responseType)) as T
    }

    override suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KClass<T>
    ): T {
        val bodyType = TypeInfo(body::class)
        val response = http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyType)
        }
        return response.body(TypeInfo(responseType)) as T
    }

    override suspend fun <T : Any, B : Any> put(path: String, body: B, responseType: KClass<T>): T {
        val bodyType = TypeInfo(body::class)
        val response = http.put(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyType)
        }
        return response.body(TypeInfo(responseType)) as T
    }

    override suspend fun <T : Any> delete(path: String, responseType: KClass<T>): T {
        val response = http.delete(path)
        return response.body(TypeInfo(responseType)) as T
    }

    override suspend fun <B : Any> postNoContent(path: String, body: B, bodyType: KClass<B>) {
        val bodyTypeInfo = TypeInfo(bodyType)
        http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyTypeInfo)
        }
    }

    override suspend fun deleteNoContent(path: String) {
        http.delete(path)
    }
}