package org.example.fakeshop_clients.core.data

import org.example.fakeshop_clients.core.error_handling.NetworkError

/**
 * Axios-specific exception mapper for web platform.
 * Maps Axios client exceptions to typed NetworkError cases.
 */
class AxiosNetworkExceptionMapper : NetworkExceptionMapper {
    override fun map(exception: Exception): NetworkError {
        val axiosError = exception.asDynamic()

        return when {
            axiosError.response != null -> {
                NetworkError.HttpError(
                    code = axiosError.response.status as Int,
                    message = axiosError.response.statusText as? String,
                    body = JSON.stringify(axiosError.response.data)
                )
            }
            axiosError.code == "ECONNABORTED" -> NetworkError.Timeout
            axiosError.code == "ERR_NETWORK" -> NetworkError.NoConnection
            axiosError.message != null -> NetworkError.Unknown(axiosError.message as? String)
            else -> NetworkError.Unknown(exception.message)
        }
    }
}
