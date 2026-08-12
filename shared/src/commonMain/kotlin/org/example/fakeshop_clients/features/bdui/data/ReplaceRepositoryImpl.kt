package org.example.fakeshop_clients.features.bdui.data

import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainReplaceBindingMapper
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainReplaceDataMapper
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainReplaceLayoutMapper
import org.example.fakeshop_clients.features.bdui.domain.ReplaceRepository
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode

class ReplaceRepositoryImpl(
    private val datasource: ReplaceDatasource,
    private val bindingMapper: DataToDomainReplaceBindingMapper,
    private val layoutMapper: DataToDomainReplaceLayoutMapper,
    private val dataMapper: DataToDomainReplaceDataMapper
) : ReplaceRepository {

    override suspend fun getReplaceBindings(
        productId: String
    ): Result<List<ReplaceBinding>, NetworkError> {
        return datasource.getReplaceBindings(productId).map { responses ->
            responses.map(bindingMapper::map)
        }
    }

    override suspend fun getReplaceLayout(layoutId: String): Result<UiNode?, NetworkError> {
        return datasource.getReplaceLayout(layoutId).map { it?.let(layoutMapper::map) }
    }

    override suspend fun getReplaceData(dataId: String): Result<JsonObject?, NetworkError> {
        return datasource.getReplaceData(dataId).map { it?.let(dataMapper::map) }
    }
}
