package org.example.fakeshop_clients.features.profile.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.parseEnvelope

internal fun NetworkError.toProfileError(): ProfileError {
    if (this !is NetworkError.HttpError) return ProfileError.Network(this)

    ProfileErrorCode.fromWire(parseEnvelope()?.code)?.let { return it.error }

    return when (code) {
        in 500..599 -> ProfileError.ServerError
        429 -> ProfileError.RateLimited
        else -> ProfileError.Unknown
    }
}
