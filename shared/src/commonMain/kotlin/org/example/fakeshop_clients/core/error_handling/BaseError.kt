package org.example.fakeshop_clients.core.error_handling

sealed interface BaseError

sealed class NetworkError : BaseError {
    data class HttpError(
        val code: Int,
        val message: String?,
        val body: String?
    ) : NetworkError()

    data object Timeout : NetworkError()
    data object NoConnection : NetworkError()
    data class Unknown(val message: String?) : NetworkError()
}