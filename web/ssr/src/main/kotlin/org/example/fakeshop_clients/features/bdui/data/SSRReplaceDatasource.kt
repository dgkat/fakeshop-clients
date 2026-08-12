package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceBindingResponse
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceDataResponse
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceLayoutResponse
import org.example.fakeshop_clients.features.core.models.Cookies

/**
 * Cookie-forwarding SSR reads for the Replace feature (mirrors [SSRBduiTemplateDatasource]).
 * Layout/data reads return `null` on `404` (drift) so the route can no-op instead of erroring.
 */
interface SSRReplaceDatasource {
    suspend fun getReplaceBindings(
        productId: String,
        cookies: Cookies
    ): Result<List<ReplaceBindingResponse>, NetworkError>

    suspend fun getReplaceLayout(
        layoutId: String,
        cookies: Cookies
    ): Result<ReplaceLayoutResponse?, NetworkError>

    suspend fun getReplaceData(
        dataId: String,
        cookies: Cookies
    ): Result<ReplaceDataResponse?, NetworkError>
}
