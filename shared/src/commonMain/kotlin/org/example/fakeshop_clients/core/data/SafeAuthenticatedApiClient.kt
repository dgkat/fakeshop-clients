package org.example.fakeshop_clients.core.data

/**
 * Safe API client for authenticated endpoints that require a valid token.
 * Examples: user profile, favorites, cart operations, protected product data.
 */
class SafeAuthenticatedApiClient(
    client: ApiClient,
    exceptionMapper: NetworkExceptionMapper
) : BaseSafeApiClient(client, exceptionMapper) {

    /**
     * Clears the internal authentication token cache.
     * Should be called after logout to ensure cached tokens are invalidated.
     */
    fun clearTokenCache() = client.clearTokenCache()
}
