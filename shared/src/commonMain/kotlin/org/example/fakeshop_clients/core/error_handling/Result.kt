package org.example.fakeshop_clients.core.error_handling

sealed interface Result<out D, out E : BaseError> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : BaseError>(val error: E) : Result<Nothing, E>
}