package org.example.fakeshop_clients.features.bdui.data.mappers

import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceDataResponse

/**
 * ReplaceData's `values` is opaque JSON bound at render time — the mapper just unwraps it.
 */
class DataToDomainReplaceDataMapper {
    fun map(response: ReplaceDataResponse): JsonObject = response.values
}
