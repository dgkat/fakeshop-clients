package org.example.fakeshop_clients.core.data.fetchClient

import kotlin.reflect.KType

interface PublicApiClient {
    suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KType,
        headers: Map<String, String> = emptyMap()
    ): T
}
