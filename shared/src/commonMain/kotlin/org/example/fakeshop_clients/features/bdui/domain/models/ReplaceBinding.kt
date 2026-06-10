package org.example.fakeshop_clients.features.bdui.domain.models

/**
 * Domain wiring for a single replace slot: "for [targetSlotId], render [layoutId]
 * filled with [dataId]". Unique per `targetSlotId` within a product.
 */
data class ReplaceBinding(
    val targetSlotId: String,
    val layoutId: String,
    val dataId: String
)
