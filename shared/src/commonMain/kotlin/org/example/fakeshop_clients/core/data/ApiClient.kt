package org.example.fakeshop_clients.core.data

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface ApiClient {
    suspend fun <T : Any> get(
        path: String,
        responseType: KType,
        headers: Map<String, String> = emptyMap()
    ): T

    suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KType,
        headers: Map<String, String> = emptyMap()
    ): T

    suspend fun <T : Any, B : Any> put(
        path: String,
        body: B,
        responseType: KType,
        headers: Map<String, String> = emptyMap()
    ): T

    suspend fun <T : Any> delete(
        path: String,
        responseType: KType,
        headers: Map<String, String> = emptyMap()
    ): T

    suspend fun <B : Any> postNoContent(
        path: String,
        body: B,
        bodyType: KClass<B>,
        headers: Map<String, String> = emptyMap()
    )

    suspend fun <B : Any> putNoContent(
        path: String,
        body: B,
        bodyType: KClass<B>,
        headers: Map<String, String> = emptyMap()
    )

    suspend fun deleteNoContent(path: String, headers: Map<String, String> = emptyMap())
}

suspend inline fun <reified T : Any> ApiClient.get(
    path: String,
    headers: Map<String, String> = emptyMap()
): T {
    return get(path, typeOf<T>(), headers)
}

suspend inline fun <reified T : Any, B : Any> ApiClient.post(
    path: String,
    body: B,
    headers: Map<String, String> = emptyMap()
): T {
    return post(path, body, typeOf<T>(), headers)
}

suspend inline fun <reified T : Any, B : Any> ApiClient.put(
    path: String,
    body: B,
    headers: Map<String, String> = emptyMap()
): T {
    return put(path, body, typeOf<T>(), headers)
}

suspend inline fun <reified T : Any> ApiClient.delete(
    path: String,
    headers: Map<String, String> = emptyMap()
): T {
    return delete(path, typeOf<T>(), headers)
}

suspend inline fun <reified B : Any> ApiClient.postNoContent(
    path: String,
    body: B,
    headers: Map<String, String> = emptyMap()
) {
    return postNoContent(path, body, B::class, headers)
}

suspend inline fun <reified B : Any> ApiClient.putNoContent(
    path: String,
    body: B,
    headers: Map<String, String> = emptyMap()
) {
    return putNoContent(path, body, B::class, headers)
}
