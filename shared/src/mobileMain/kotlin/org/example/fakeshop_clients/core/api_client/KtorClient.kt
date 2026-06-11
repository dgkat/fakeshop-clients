package org.example.fakeshop_clients.core.api_client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo
import org.example.fakeshop_clients.core.auth.data.TokenCacheInvalidator
import org.example.fakeshop_clients.core.data.ApiClient
import kotlin.reflect.KClass
import kotlin.reflect.KType

class KtorClient(val http: HttpClient) : ApiClient, TokenCacheInvalidator {

    /**
     * Builds Ktor [TypeInfo] from a full [KType] so generic responses (e.g. `List<Foo>`)
     * keep their type argument — a bare `KClass` would erase it and break deserialization.
     */
    private fun KType.toTypeInfo(): TypeInfo = TypeInfo(classifier as KClass<*>, this)

    override suspend fun <T : Any> get(path: String, responseType: KType): T {
        val response = http.get(path)
        return response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KType
    ): T {
        val bodyType = TypeInfo(body::class)
        val response = http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyType)
        }
        return response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <T : Any, B : Any> postWithHeaders(
        path: String,
        body: B,
        headers: Map<String, String>,
        responseType: KType
    ): T {
        val bodyType = TypeInfo(body::class)
        val response = http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyType)
            headers.forEach { (key, value) -> header(key, value) }
        }
        return response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <T : Any, B : Any> put(path: String, body: B, responseType: KType): T {
        val bodyType = TypeInfo(body::class)
        val response = http.put(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyType)
        }
        return response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <T : Any> delete(path: String, responseType: KType): T {
        val response = http.delete(path)
        return response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <B : Any> postNoContent(path: String, body: B, bodyType: KClass<B>) {
        val bodyTypeInfo = TypeInfo(bodyType)
        http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyTypeInfo)
        }
    }

    override suspend fun <B : Any> putNoContent(path: String, body: B, bodyType: KClass<B>) {
        val bodyTypeInfo = TypeInfo(bodyType)
        http.put(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyTypeInfo)
        }
    }

    override suspend fun deleteNoContent(path: String) {
        http.delete(path)
    }

    override fun invalidate() {
        http.authProvider<BearerAuthProvider>()?.clearToken()
    }
}