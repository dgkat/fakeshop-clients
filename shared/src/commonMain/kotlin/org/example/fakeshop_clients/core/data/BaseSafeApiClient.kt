package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

/**
 * Base safe API client that wraps all API calls in Result type for type-safe error handling.
 * Automatically catches exceptions and converts them to typed NetworkError using the provided mapper.
 */
abstract class BaseSafeApiClient(
    val client: ApiClient,
    val exceptionMapper: NetworkExceptionMapper
)

/**
 * Extension functions that provide inline reified type parameters for the safe API client.
 */
suspend inline fun <reified T : Any> BaseSafeApiClient.get(path: String): Result<T, NetworkError> =
    safeResult(exceptionMapper) { client.get(path) }

suspend inline fun <reified T : Any, B : Any> BaseSafeApiClient.post(
    path: String,
    body: B
): Result<T, NetworkError> =
    safeResult(exceptionMapper) { client.post(path, body) }

suspend inline fun <reified T : Any, B : Any> BaseSafeApiClient.postWithHeaders(
    path: String,
    body: B,
    headers: Map<String, String>
): Result<T, NetworkError> =
    safeResult(exceptionMapper) { client.postWithHeaders(path, body, headers) }

suspend inline fun <reified T : Any, B : Any> BaseSafeApiClient.put(
    path: String,
    body: B
): Result<T, NetworkError> =
    safeResult(exceptionMapper) { client.put(path, body) }

suspend inline fun <reified T : Any> BaseSafeApiClient.delete(path: String): Result<T, NetworkError> =
    safeResult(exceptionMapper) { client.delete(path) }

suspend inline fun <reified B : Any> BaseSafeApiClient.postNoContent(path: String, body: B): Result<Unit, NetworkError> =
    safeResult(exceptionMapper) { client.postNoContent(path, body) }

suspend inline fun <reified B : Any> BaseSafeApiClient.putNoContent(path: String, body: B): Result<Unit, NetworkError> =
    safeResult(exceptionMapper) { client.putNoContent(path, body) }

suspend fun BaseSafeApiClient.deleteNoContent(path: String): Result<Unit, NetworkError> =
    safeResult(exceptionMapper) { client.deleteNoContent(path) }
