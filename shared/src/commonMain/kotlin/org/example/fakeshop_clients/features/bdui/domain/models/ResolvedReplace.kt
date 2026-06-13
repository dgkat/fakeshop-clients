package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.json.JsonObject

/**
 * A fully resolved replacement, ready for the renderer to swap in: the layout's UI
 * subtree [node] bound against its own standalone [values] namespace (never merged
 * into the product `bindData`).
 */
data class ResolvedReplace(
    val node: UiNode,
    val values: JsonObject
) {
    /**
     * iOS-only convenience: the standalone [values] pre-wrapped as [BindData].
     *
     * The iOS renderer MUST use this instead of `BindData(json: values)`: reading `values` from
     * Swift bridges the [JsonObject] to a Swift `Dictionary` and back, which breaks path resolution
     * (every bind resolves to null → the replacement renders blank). Wrapped here, the JsonObject
     * stays Kotlin-side. Android renders `values` directly in pure Kotlin and ignores this.
     */
    val bindData: BindData get() = BindData(values)
}
