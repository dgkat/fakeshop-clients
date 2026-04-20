package org.example.fakeshop_clients.features.profile.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface ProfileError {
    // Auth (login)
    data object InvalidCredentials : ProfileError
    data object AccountLocked : ProfileError
    data object AccountNotVerified : ProfileError

    // Users (sign-up)
    data object EmailAlreadyExists : ProfileError
    data object InvalidEmailFormat : ProfileError
    data object EmailTooLong : ProfileError
    data object WeakPassword : ProfileError
    data object PasswordTooShort : ProfileError
    data object PasswordTooLong : ProfileError

    // Shared / infrastructure
    data object RateLimited : ProfileError
    data object ServerError : ProfileError
    data class Network(val error: NetworkError) : ProfileError
    data object Unknown : ProfileError
}

/**
 * Wire-format error codes emitted by the backend in `{code, message}` envelopes.
 * Enum name == wire code — keep them in sync with the BE's AuthError / UserError.
 */
enum class ProfileErrorCode(val error: ProfileError) {
    INVALID_CREDENTIALS(ProfileError.InvalidCredentials),
    ACCOUNT_LOCKED(ProfileError.AccountLocked),
    ACCOUNT_NOT_VERIFIED(ProfileError.AccountNotVerified),
    EMAIL_ALREADY_EXISTS(ProfileError.EmailAlreadyExists),
    INVALID_EMAIL_FORMAT(ProfileError.InvalidEmailFormat),
    EMAIL_TOO_LONG(ProfileError.EmailTooLong),
    WEAK_PASSWORD(ProfileError.WeakPassword),
    PASSWORD_TOO_SHORT(ProfileError.PasswordTooShort),
    PASSWORD_TOO_LONG(ProfileError.PasswordTooLong),
    RATE_LIMITED(ProfileError.RateLimited);

    companion object {
        fun fromWire(code: String?): ProfileErrorCode? =
            code?.let { value -> entries.firstOrNull { it.name == value } }
    }
}
