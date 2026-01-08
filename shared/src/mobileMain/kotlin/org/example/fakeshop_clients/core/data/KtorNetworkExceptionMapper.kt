package org.example.fakeshop_clients.core.data

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException
import org.example.fakeshop_clients.core.error_handling.NetworkError

/**
 * Ktor-specific exception mapper for mobile platforms (Android/iOS).
 * Maps Ktor client exceptions to typed NetworkError cases.
 */
class KtorNetworkExceptionMapper : NetworkExceptionMapper {
    override fun map(exception: Exception): NetworkError {
        return when (exception) {
            is ResponseException -> NetworkError.HttpError(
                code = exception.response.status.value,
                message = exception.response.status.description,
                body = null
            )

            is HttpRequestTimeoutException -> NetworkError.Timeout
            is ConnectTimeoutException -> NetworkError.Timeout
            is SocketTimeoutException -> NetworkError.Timeout
            is UnresolvedAddressException -> NetworkError.NoConnection
            is IOException -> NetworkError.NoConnection
            else -> NetworkError.Unknown(exception.message)
        }
    }
}
