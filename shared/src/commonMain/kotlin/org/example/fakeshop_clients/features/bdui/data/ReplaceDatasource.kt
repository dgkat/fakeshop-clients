package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceBindingResponse
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceDataResponse
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceLayoutResponse

/**
 * Public runtime reads for the Replace feature. All three endpoints are
 * guest/optional-auth (no `/admin`).
 *
 * Drift contract: a `404` on a layout/data read means the resource was deleted and the
 * binding now dangles — these methods surface that as `Result.Success(null)` (a no-op
 * signal) rather than a [NetworkError]. Bindings never `404` (unknown product → `200 []`).
 */
interface ReplaceDatasource {
    /** `GET /products/{id}/replace-bindings` — always `200`; `[]` for unknown/no bindings. */
    suspend fun getReplaceBindings(productId: String): Result<List<ReplaceBindingResponse>, NetworkError>

    /** `GET /replace-layouts/{id}` — `404` (drift) → `Success(null)`. */
    suspend fun getReplaceLayout(layoutId: String): Result<ReplaceLayoutResponse?, NetworkError>

    /** `GET /replace-data/{id}` — `404` (drift) → `Success(null)`. */
    suspend fun getReplaceData(dataId: String): Result<ReplaceDataResponse?, NetworkError>
}
