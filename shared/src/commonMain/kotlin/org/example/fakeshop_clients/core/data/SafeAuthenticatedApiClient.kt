package org.example.fakeshop_clients.core.data

/**
 * Safe API client for authenticated endpoints that require a valid token.
 * Examples: user profile, favorites, cart operations, protected product data.
 */
class SafeAuthenticatedApiClient(
    client: ApiClient,
    exceptionMapper: NetworkExceptionMapper
) : BaseSafeApiClient(client, exceptionMapper)
