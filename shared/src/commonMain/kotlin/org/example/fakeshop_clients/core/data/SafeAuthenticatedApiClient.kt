package org.example.fakeshop_clients.core.data

class SafeAuthenticatedApiClient(
    client: ApiClient,
    exceptionMapper: NetworkExceptionMapper
) : BaseSafeApiClient(client, exceptionMapper)
