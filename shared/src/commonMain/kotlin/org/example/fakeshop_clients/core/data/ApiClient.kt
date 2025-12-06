package org.example.fakeshop_clients.core.data

import kotlin.reflect.KClass

interface ApiClient {
    suspend fun <T : Any> get(path: String, responseType: KClass<T>): T
    suspend fun <T : Any, B : Any> post(path: String, body: B, responseType: KClass<T>): T
    suspend fun <T : Any, B : Any> put(path: String, body: B, responseType: KClass<T>): T
    suspend fun <T : Any> delete(path: String, responseType: KClass<T>): T
}

suspend inline fun <reified T : Any> ApiClient.get(path: String): T {
    return get(path, T::class)
}

suspend inline fun <reified T : Any, B : Any> ApiClient.post(path: String, body: B): T {
    return post(path, body, T::class)
}

suspend inline fun <reified T : Any, B : Any> ApiClient.put(path: String, body: B): T {
    return put(path, body, T::class)
}

suspend inline fun <reified T : Any> ApiClient.delete(path: String): T {
    return delete(path, T::class)
}