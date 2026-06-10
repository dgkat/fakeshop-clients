package org.example.fakeshop_clients.features.bdui.domain

import org.example.fakeshop_clients.features.bdui.domain.models.ResolvedReplace
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode

/**
 * Render-time helper for the **standalone-scope** Replace swap.
 *
 * The replacement is applied at render time, not by mutating the template tree: each
 * platform renderer, as it walks the [UiNode] tree, asks [replacementFor] whether the
 * current node is a replaced slot. If so it renders [ResolvedReplace.node] against a fresh
 * `BindData(values)` instead of the original subtree + product `bindData`. This keeps the
 * replacement self-contained and avoids bind-key collisions.
 *
 * Slot matching mirrors the container set handled by
 * [BduiMutationApplier.applyReplaceSlot] (Column / Row / Section) — a slot is an authored
 * container subtree.
 */
object BduiReplaceResolver {

    /** The `slotId` of a container node, or `null` for non-container / unslotted nodes. */
    fun slotIdOf(node: UiNode): String? = when (node) {
        is UiNode.Column -> node.slotId
        is UiNode.Row -> node.slotId
        is UiNode.Section -> node.slotId
        else -> null
    }

    /**
     * The replacement to render in place of [node], or `null` to render [node] normally.
     * Returns a hit only when [node] is a container whose `slotId` is in [replacedSlots].
     */
    fun replacementFor(
        node: UiNode,
        replacedSlots: Map<String, ResolvedReplace>
    ): ResolvedReplace? {
        if (replacedSlots.isEmpty()) return null
        return slotIdOf(node)?.let { replacedSlots[it] }
    }
}
