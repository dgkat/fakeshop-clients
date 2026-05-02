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
suspend inline fun <reified T : Any> BaseSafeApiClient.get(path: String): Result<T, NetworkError> {
    return try {
        Result.Success(client.get(path))
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}

suspend inline fun <reified T : Any, B : Any> BaseSafeApiClient.post(
    path: String,
    body: B
): Result<T, NetworkError> {
    return try {
        Result.Success(client.post(path, body))
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}

suspend inline fun <reified T : Any, B : Any> BaseSafeApiClient.postWithHeaders(
    path: String,
    body: B,
    headers: Map<String, String>
): Result<T, NetworkError> {
    return try {
        Result.Success(client.postWithHeaders(path, body, headers))
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}

suspend inline fun <reified T : Any, B : Any> BaseSafeApiClient.put(
    path: String,
    body: B
): Result<T, NetworkError> {
    return try {
        Result.Success(client.put(path, body))
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}

suspend inline fun <reified T : Any> BaseSafeApiClient.delete(path: String): Result<T, NetworkError> {
    return try {
        Result.Success(client.delete(path))
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}

suspend inline fun <reified B : Any> BaseSafeApiClient.postNoContent(path: String, body: B): Result<Unit, NetworkError> {
    return try {
        client.postNoContent(path, body)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}

suspend inline fun <reified B : Any> BaseSafeApiClient.putNoContent(path: String, body: B): Result<Unit, NetworkError> {
    return try {
        client.putNoContent(path, body)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}

suspend fun BaseSafeApiClient.deleteNoContent(path: String): Result<Unit, NetworkError> {
    return try {
        client.deleteNoContent(path)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(exceptionMapper.map(e))
    }
}
