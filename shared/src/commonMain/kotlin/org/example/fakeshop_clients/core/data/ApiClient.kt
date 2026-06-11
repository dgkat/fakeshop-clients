package org.example.fakeshop_clients.core.data

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface ApiClient {
    suspend fun <T : Any> get(path: String, responseType: KType): T
    suspend fun <T : Any, B : Any> post(path: String, body: B, responseType: KType): T
    suspend fun <T : Any, B : Any> postWithHeaders(
        path: String,
        body: B,
        headers: Map<String, String>,
        responseType: KType
    ): T = post(path, body, responseType)
    suspend fun <T : Any, B : Any> put(path: String, body: B, responseType: KType): T
    suspend fun <T : Any> delete(path: String, responseType: KType): T
    suspend fun <B : Any> postNoContent(path: String, body: B, bodyType: KClass<B>)
    suspend fun <B : Any> putNoContent(path: String, body: B, bodyType: KClass<B>)
    suspend fun deleteNoContent(path: String)
}

suspend inline fun <reified T : Any> ApiClient.get(path: String): T {
    return get(path, typeOf<T>())
}

suspend inline fun <reified T : Any, B : Any> ApiClient.post(path: String, body: B): T {
    return post(path, body, typeOf<T>())
}

suspend inline fun <reified T : Any, B : Any> ApiClient.postWithHeaders(
    path: String,
    body: B,
    headers: Map<String, String>
): T = postWithHeaders(path, body, headers, typeOf<T>())

suspend inline fun <reified T : Any, B : Any> ApiClient.put(path: String, body: B): T {
    return put(path, body, typeOf<T>())
}

suspend inline fun <reified T : Any> ApiClient.delete(path: String): T {
    return delete(path, typeOf<T>())
}

suspend inline fun <reified B : Any> ApiClient.postNoContent(path: String, body: B) {
    return postNoContent(path, body, B::class)
}

suspend inline fun <reified B : Any> ApiClient.putNoContent(path: String, body: B) {
    return putNoContent(path, body, B::class)
}