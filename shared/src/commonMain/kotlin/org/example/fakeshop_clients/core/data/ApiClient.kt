package org.example.fakeshop_clients.core.data

import kotlin.reflect.KClass

interface ApiClient {
    suspend fun <T : Any> get(path: String, responseType: KClass<T>): T
    suspend fun <T : Any, B : Any> post(path: String, body: B, responseType: KClass<T>): T
    suspend fun <T : Any, B : Any> postWithHeaders(
        path: String,
        body: B,
        headers: Map<String, String>,
        responseType: KClass<T>
    ): T = post(path, body, responseType)
    suspend fun <T : Any, B : Any> put(path: String, body: B, responseType: KClass<T>): T
    suspend fun <T : Any> delete(path: String, responseType: KClass<T>): T
    suspend fun <B : Any> postNoContent(path: String, body: B, bodyType: KClass<B>)
    suspend fun <B : Any> putNoContent(path: String, body: B, bodyType: KClass<B>)
    suspend fun deleteNoContent(path: String)

    fun clearTokenCache() {}
}

suspend inline fun <reified T : Any> ApiClient.get(path: String): T {
    return get(path, T::class)
}

suspend inline fun <reified T : Any, B : Any> ApiClient.post(path: String, body: B): T {
    return post(path, body, T::class)
}

suspend inline fun <reified T : Any, B : Any> ApiClient.postWithHeaders(
    path: String,
    body: B,
    headers: Map<String, String>
): T = postWithHeaders(path, body, headers, T::class)

suspend inline fun <reified T : Any, B : Any> ApiClient.put(path: String, body: B): T {
    return put(path, body, T::class)
}

suspend inline fun <reified T : Any> ApiClient.delete(path: String): T {
    return delete(path, T::class)
}

suspend inline fun <reified B : Any> ApiClient.postNoContent(path: String, body: B) {
    return postNoContent(path, body, B::class)
}

suspend inline fun <reified B : Any> ApiClient.putNoContent(path: String, body: B) {
    return putNoContent(path, body, B::class)
}