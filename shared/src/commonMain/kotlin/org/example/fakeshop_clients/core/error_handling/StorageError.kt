package org.example.fakeshop_clients.core.error_handling

sealed interface StorageError : BaseError {
    data class WriteFailed(val message: String?, val cause: Throwable? = null) : StorageError
}
