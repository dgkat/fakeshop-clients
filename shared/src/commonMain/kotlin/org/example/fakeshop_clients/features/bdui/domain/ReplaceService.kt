package org.example.fakeshop_clients.features.bdui.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding
import org.example.fakeshop_clients.features.bdui.domain.models.ResolvedReplace

interface ReplaceService {
    /** The product's replace wiring (the 4th, non-blocking PDP call). `[]` when none. */
    suspend fun getReplaceBindings(productId: String): Result<List<ReplaceBinding>, NetworkError>

    /**
     * Resolves the replacement for [targetSlotId] from [bindings]: fetches the layout + data
     * in parallel and combines them. `Success(null)` means **no-op** — no binding for the slot,
     * or the layout/data drifted (deleted). Only a transport failure surfaces as an error.
     */
    suspend fun resolve(
        bindings: List<ReplaceBinding>,
        targetSlotId: String
    ): Result<ResolvedReplace?, NetworkError>
}
