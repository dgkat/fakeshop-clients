package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.data.SSRSafeApiClient
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceBindingResponse
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceDataResponse
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceLayoutResponse
import org.example.fakeshop_clients.features.core.models.Cookies

class SSRReplaceDatasourceImpl(
    private val safeApiClient: SSRSafeApiClient,
    private val baseUrl: UrlProvider
) : SSRReplaceDatasource {

    // Always 200 ([] for unknown product / no bindings) — only a transport failure is an error.
    override suspend fun getReplaceBindings(
        productId: String,
        cookies: Cookies
    ): Result<List<ReplaceBindingResponse>, NetworkError> =
        safeApiClient.get(
            path = "${baseUrl()}/products/$productId/replace-bindings",
            cookies = cookies
        )

    override suspend fun getReplaceLayout(
        layoutId: String,
        cookies: Cookies
    ): Result<ReplaceLayoutResponse?, NetworkError> =
        safeApiClient.get<ReplaceLayoutResponse>(
            path = "${baseUrl()}/replace-layouts/$layoutId",
            cookies = cookies
        ).driftAware()

    override suspend fun getReplaceData(
        dataId: String,
        cookies: Cookies
    ): Result<ReplaceDataResponse?, NetworkError> =
        safeApiClient.get<ReplaceDataResponse>(
            path = "${baseUrl()}/replace-data/$dataId",
            cookies = cookies
        ).driftAware()

    /** Folds a `404` (deleted layout/data = drift) into `Success(null)`; other errors propagate. */
    private fun <T> Result<T, NetworkError>.driftAware(): Result<T?, NetworkError> = when (this) {
        is Result.Success -> this
        is Result.Error -> {
            val err = error
            if (err is NetworkError.HttpError && err.code == 404) Result.Success(null) else this
        }
    }
}
