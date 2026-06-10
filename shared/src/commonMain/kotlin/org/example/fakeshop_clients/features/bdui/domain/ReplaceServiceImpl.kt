package org.example.fakeshop_clients.features.bdui.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.zip
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding
import org.example.fakeshop_clients.features.bdui.domain.models.ResolvedReplace

class ReplaceServiceImpl(
    private val repository: ReplaceRepository
) : ReplaceService {

    override suspend fun getReplaceBindings(
        productId: String
    ): Result<List<ReplaceBinding>, NetworkError> {
        return repository.getReplaceBindings(productId)
    }

    override suspend fun resolve(
        bindings: List<ReplaceBinding>,
        targetSlotId: String
    ): Result<ResolvedReplace?, NetworkError> {
        // Bindings are unique per slot, so at most one matches. None → no-op.
        val binding = bindings.firstOrNull { it.targetSlotId == targetSlotId }
            ?: return Result.Success(null)

        return coroutineScope {
            val layoutDef = async { repository.getReplaceLayout(binding.layoutId) }
            val dataDef = async { repository.getReplaceData(binding.dataId) }

            // zip surfaces the first transport error; drift (null on either) collapses to no-op.
            layoutDef.await().zip(dataDef.await()) { node, values ->
                if (node == null || values == null) null else ResolvedReplace(node, values)
            }
        }
    }
}
