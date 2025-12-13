package org.example.fakeshop_clients.core.data.fetchClient

import kotlin.reflect.KClass

interface PublicApiClient {
    suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KClass<T>
    ): T
}