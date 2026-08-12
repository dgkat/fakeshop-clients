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
import kotlinx.coroutines.withContext
import org.example.fakeshop_clients.core.auth.data.TokenCacheInvalidator
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import org.example.fakeshop_clients.core.data.ApiClient
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * @param dispatcherProvider the request + response deserialization run on [DispatcherProvider.default],
 * NOT on the caller's dispatcher. On iOS the ViewStore scope is the main thread
 * ([kotlinx.coroutines.MainScope]); without this the polymorphic JSON parse (e.g. the BDUI `UiNode`
 * tree) would deserialize on the main thread and jam the UI. `default` is the CPU pool — the right
 * place for deserialization (the engine runs the network on its own threads regardless). On iOS
 * `default` and `io` are both `Dispatchers.Default` (no dedicated IO dispatcher).
 */
class KtorClient(
    val http: HttpClient,
    private val dispatcherProvider: DispatcherProvider
) : ApiClient, TokenCacheInvalidator {

    /**
     * Builds Ktor [TypeInfo] from a full [KType] so generic responses (e.g. `List<Foo>`)
     * keep their type argument — a bare `KClass` would erase it and break deserialization.
     *
     * Note: `kotlinType` MUST be set explicitly. Kotlin/Native has no reflection fallback, so the
     * kotlinx.serialization converter resolves the serializer from `kotlinType`; leaving it null
     * (the deprecated `reifiedType` constructor) erases the generic arg and fails on iOS with
     * "Serializer for class 'List' is not found" while still working on the JVM.
     */
    private fun KType.toTypeInfo(): TypeInfo = TypeInfo(classifier as KClass<*>, kotlinType = this)

    override suspend fun <T : Any> get(path: String, responseType: KType): T = withContext(dispatcherProvider.default) {
        val response = http.get(path)
        response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KType
    ): T = withContext(dispatcherProvider.default) {
        val bodyType = TypeInfo(body::class)
        val response = http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyType)
        }
        response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <T : Any, B : Any> postWithHeaders(
        path: String,
        body: B,
        headers: Map<String, String>,
        responseType: KType
    ): T = withContext(dispatcherProvider.default) {
        val bodyType = TypeInfo(body::class)
        val response = http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyType)
            headers.forEach { (key, value) -> header(key, value) }
        }
        response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <T : Any, B : Any> put(path: String, body: B, responseType: KType): T = withContext(dispatcherProvider.default) {
        val bodyType = TypeInfo(body::class)
        val response = http.put(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyType)
        }
        response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <T : Any> delete(path: String, responseType: KType): T = withContext(dispatcherProvider.default) {
        val response = http.delete(path)
        response.body(responseType.toTypeInfo()) as T
    }

    override suspend fun <B : Any> postNoContent(path: String, body: B, bodyType: KClass<B>): Unit = withContext(dispatcherProvider.default) {
        val bodyTypeInfo = TypeInfo(bodyType)
        http.post(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyTypeInfo)
        }
    }

    override suspend fun <B : Any> putNoContent(path: String, body: B, bodyType: KClass<B>): Unit = withContext(dispatcherProvider.default) {
        val bodyTypeInfo = TypeInfo(bodyType)
        http.put(path) {
            contentType(ContentType.Application.Json)
            setBody(body, bodyTypeInfo)
        }
    }

    override suspend fun deleteNoContent(path: String): Unit = withContext(dispatcherProvider.default) {
        http.delete(path)
    }

    override fun invalidate() {
        http.authProvider<BearerAuthProvider>()?.clearToken()
    }
}