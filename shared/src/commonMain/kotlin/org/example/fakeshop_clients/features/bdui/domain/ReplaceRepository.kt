package org.example.fakeshop_clients.features.bdui.domain

import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode

/**
 * Domain access to the Replace runtime reads. Layout/data reads return `null` on drift
 * (deleted resource) — see [ReplaceDatasource][org.example.fakeshop_clients.features.bdui.data.ReplaceDatasource].
 */
interface ReplaceRepository {
    suspend fun getReplaceBindings(productId: String): Result<List<ReplaceBinding>, NetworkError>
    suspend fun getReplaceLayout(layoutId: String): Result<UiNode?, NetworkError>
    suspend fun getReplaceData(dataId: String): Result<JsonObject?, NetworkError>
}
