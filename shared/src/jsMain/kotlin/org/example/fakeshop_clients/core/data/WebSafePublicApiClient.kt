package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.data.fetchClient.PublicApiClient
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

/**
 * Web-specific safe public API client that wraps the fetch-based PublicApiClient.
 * Used only for auth operations (login, signup, refresh) to avoid circular dependencies
 * with AxiosClient's auth interceptor.
 */
class WebSafePublicApiClient(
    val publicApiClient: PublicApiClient,
    val exceptionMapper: NetworkExceptionMapper
)

/**
 * Extension function for type-safe POST requests.
 */
suspend inline fun <reified T : Any, B : Any> WebSafePublicApiClient.post(
    path: String,
    body: B
): Result<T, NetworkError> {
    return try {
        Result.Success(publicApiClient.post(path, body, T::class))
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}

suspend inline fun <reified T : Any, B : Any> WebSafePublicApiClient.postWithHeaders(
    path: String,
    body: B,
    headers: Map<String, String>
): Result<T, NetworkError> {
    return try {
        Result.Success(publicApiClient.postWithHeaders(path, body, headers, T::class))
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}
