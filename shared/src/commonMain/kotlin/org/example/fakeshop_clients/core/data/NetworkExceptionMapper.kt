package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.error_handling.NetworkError

/**
 * Maps platform-specific exceptions to typed NetworkError cases.
 * Each platform (Ktor for mobile, Axios for web) provides its own implementation.
 */
interface NetworkExceptionMapper {
    fun map(exception: Exception): NetworkError
}
