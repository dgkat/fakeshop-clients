package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import kotlin.coroutines.cancellation.CancellationException

inline fun <T> safeResult(
    mapper: NetworkExceptionMapper,
    block: () -> T
): Result<T, NetworkError> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Error(mapper.map(e))
    }
}
