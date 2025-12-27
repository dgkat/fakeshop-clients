package org.example.fakeshop_clients.core.data

/**
 * Safe API client for public endpoints that don't require authentication.
 * Examples: login, register, forgot password, public product listings.
 */
class SafePublicApiClient(
    client: ApiClient,
    exceptionMapper: NetworkExceptionMapper
) : BaseSafeApiClient(client, exceptionMapper)
