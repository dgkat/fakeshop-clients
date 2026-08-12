package org.example.fakeshop_clients.core.data.fetchClient

import kotlin.reflect.KType

interface PublicApiClient {
    suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KType
    ): T

    suspend fun <T : Any, B : Any> postWithHeaders(
        path: String,
        body: B,
        headers: Map<String, String>,
        responseType: KType
    ): T = post(path, body, responseType)
}