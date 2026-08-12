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
 * Slot matching covers every node type the CMS can author a `slotId` on — every variant
 * except [UiNode.Divider]. This stays in sync with the set handled by
 * [BduiMutationApplier.applyReplaceSlot] via the shared [slotIdOf] helper, so a slot may be
 * any authored subtree (container or leaf).
 */
object BduiReplaceResolver {

    /**
     * The `slotId` of [node], or `null` for an unslotted node ([UiNode.Divider] is never slotted).
     * `slotId` is a first-class [UiNode] property, so this is now a direct read — the old
     * hand-maintained per-type `when` is gone and new node types are covered automatically.
     */
    fun slotIdOf(node: UiNode): String? = node.slotId

    /**
     * The replacement to render in place of [node], or `null` to render [node] normally.
     * Returns a hit only when [node]'s `slotId` is in [replacedSlots].
     */
    fun replacementFor(
        node: UiNode,
        replacedSlots: Map<String, ResolvedReplace>
    ): ResolvedReplace? {
        if (replacedSlots.isEmpty()) return null
        return slotIdOf(node)?.let { replacedSlots[it] }
    }
}
