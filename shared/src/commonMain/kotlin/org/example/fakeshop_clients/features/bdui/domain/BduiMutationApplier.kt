package org.example.fakeshop_clients.features.bdui.domain

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.BduiMutation
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode

object BduiMutationApplier {

    /**
     * Deep-merges an UpdateBindData patch into the current bindData.
     * Top-level keys from the patch overwrite or recursively merge into bindData.
     */
    fun applyBindPatch(current: JsonObject, patch: JsonObject): JsonObject {
        return deepMerge(current, patch) as JsonObject
    }

    /**
     * Walks the UiNode tree and replaces the first node whose slotId matches.
     * Returns the original tree unchanged if no matching slotId is found.
     */
    fun applyReplaceSlot(root: UiNode, mutation: BduiMutation.ReplaceSlot): UiNode {
        return replaceInNode(root, mutation.slotId, mutation.node)
    }

    private fun deepMerge(base: JsonElement, override: JsonElement): JsonElement {
        if (base !is JsonObject || override !is JsonObject) return override
        val merged = base.toMutableMap()
        for ((key, value) in override) {
            merged[key] = if (key in merged) deepMerge(merged.getValue(key), value) else value
        }
        return JsonObject(merged)
    }

    private fun replaceInNode(node: UiNode, slotId: String, replacement: UiNode): UiNode {
        return when (node) {
            is UiNode.Column -> {
                if (node.slotId == slotId) replacement
                else node.copy(children = node.children.map { replaceInNode(it, slotId, replacement) })
            }
            is UiNode.Row -> {
                if (node.slotId == slotId) replacement
                else node.copy(children = node.children.map { replaceInNode(it, slotId, replacement) })
            }
            is UiNode.Section -> {
                if (node.slotId == slotId) replacement
                else node.copy(children = node.children.map { replaceInNode(it, slotId, replacement) })
            }
            else -> node
        }
    }
}
