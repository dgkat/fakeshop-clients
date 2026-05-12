package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainBduiTemplateMapper
import org.example.fakeshop_clients.features.bdui.domain.BduiTemplateRepository
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate

class BduiTemplateRepositoryImpl(
    private val datasource: BduiTemplateDatasource,
    private val mapper: DataToDomainBduiTemplateMapper
) : BduiTemplateRepository {

    override suspend fun getPdpTemplate(category: String): Result<BduiTemplate, NetworkError> {
        return datasource.getPdpTemplate(category).map { mapper.map(it) }
    }
}
