package org.example.fakeshop_clients.features.bdui.data.mappers

import org.example.fakeshop_clients.features.bdui.data.models.ReplaceBindingResponse
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding

class DataToDomainReplaceBindingMapper {
    fun map(response: ReplaceBindingResponse): ReplaceBinding = ReplaceBinding(
        targetSlotId = response.targetSlotId,
        layoutId = response.layoutId,
        dataId = response.dataId
    )
}
