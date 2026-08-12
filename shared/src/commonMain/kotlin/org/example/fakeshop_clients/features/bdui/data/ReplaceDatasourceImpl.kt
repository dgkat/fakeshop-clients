package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceBindingResponse
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceDataResponse
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceLayoutResponse

class ReplaceDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : ReplaceDatasource {

    override suspend fun getReplaceBindings(
        productId: String
    ): Result<List<ReplaceBindingResponse>, NetworkError> {
        return authClient.get(path = "${baseUrl()}/products/$productId/replace-bindings")
    }

    override suspend fun getReplaceLayout(
        layoutId: String
    ): Result<ReplaceLayoutResponse?, NetworkError> {
        return authClient.get<ReplaceLayoutResponse>(
            path = "${baseUrl()}/replace-layouts/$layoutId"
        ).driftAware()
    }

    override suspend fun getReplaceData(
        dataId: String
    ): Result<ReplaceDataResponse?, NetworkError> {
        return authClient.get<ReplaceDataResponse>(
            path = "${baseUrl()}/replace-data/$dataId"
        ).driftAware()
    }

    /**
     * Folds a `404` (the resource was deleted — binding drift) into `Success(null)` so the
     * service can no-op. Any other [NetworkError] propagates unchanged.
     */
    private fun <T> Result<T, NetworkError>.driftAware(): Result<T?, NetworkError> = when (this) {
        is Result.Success -> this
        is Result.Error -> {
            val err = error
            if (err is NetworkError.HttpError && err.code == 404) Result.Success(null) else this
        }
    }
}
