package org.example.fakeshop_clients.core.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.cookie
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.core.models.Cookies


class SSRSafeApiClient(
    val httpClient: HttpClient,
    val exceptionMapper: NetworkExceptionMapper
) {

    /**
     * Performs a GET request with cookie forwarding and automatic error handling.
     */
    suspend inline fun <reified T : Any> get(
        path: String,
        cookies: Cookies
    ): Result<T, NetworkError> {
        return try {
            val response = httpClient.get(path) {
                configureCookiesAndAuth(cookies)
            }
            Result.Success(response.body(TypeInfo(T::class)))
        } catch (e: Exception) {
            Result.Error(exceptionMapper.map(e))
        }
    }

    /**
     * Performs a POST request with body, cookie forwarding and automatic error handling.
     */
    suspend inline fun <reified T : Any, B : Any> post(
        path: String,
        body: B,
        cookies: Cookies
    ): Result<T, NetworkError> {
        return try {
            val bodyType = TypeInfo(body::class)
            val response = httpClient.post(path) {
                contentType(ContentType.Application.Json)
                setBody(body, bodyType)
                configureCookiesAndAuth(cookies)
            }
            Result.Success(response.body(TypeInfo(T::class)))
        } catch (e: Exception) {
            Result.Error(exceptionMapper.map(e))
        }
    }

    /**
     * Performs a POST request without body (for operations like toggle like).
     */
    suspend inline fun <reified T : Any> post(
        path: String,
        cookies: Cookies
    ): Result<T, NetworkError> {
        return try {
            val response = httpClient.post(path) {
                configureCookiesAndAuth(cookies)
            }
            Result.Success(response.body(TypeInfo(T::class)))
        } catch (e: Exception) {
            Result.Error(exceptionMapper.map(e))
        }
    }

    /**
     * Performs a DELETE request with cookie forwarding and automatic error handling.
     */
    suspend inline fun <reified T : Any> delete(
        path: String,
        cookies: Cookies
    ): Result<T, NetworkError> {
        return try {
            val response = httpClient.delete(path) {
                configureCookiesAndAuth(cookies)
            }
            Result.Success(response.body(TypeInfo(T::class)))
        } catch (e: Exception) {
            Result.Error(exceptionMapper.map(e))
        }
    }

    /**
     * Configures cookies for SSR requests to backend API.
     * Forwards all cookies (including session ID) from the user's browser to the backend.
     * The backend validates the session cookie for authentication.
     */
    @PublishedApi
    internal fun HttpRequestBuilder.configureCookiesAndAuth(cookies: Cookies) {
        // Forward all cookies from the user's browser to the backend
        // This includes the session ID cookie that the backend uses for authentication
        cookies.data.forEach { (name, value) ->
            cookie(name, value)
        }
    }
}
