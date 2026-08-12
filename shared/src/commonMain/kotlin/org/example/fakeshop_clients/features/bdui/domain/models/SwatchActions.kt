package org.example.fakeshop_clients.features.bdui.domain.models

/**
 * Per-swatch action resolution for [UiNode.ColorSwatchPicker]. Single source of truth shared by
 * every renderer (Android / iOS / web SSR) so the "which action does this color fire" decision
 * stays out of per-platform UI code.
 */

/**
 * The effective [NodeAction] for the swatch named [colorName]:
 * the per-color override from [UiNode.ColorSwatchPicker.swatchActions] if present, otherwise the
 * node's default ([UiNode.ColorSwatchPicker.actionId] + [UiNode.ColorSwatchPicker.contextBindings]).
 * Returns `null` when there is no override and no non-blank default — a swatch with no action is inert.
 */
fun UiNode.ColorSwatchPicker.actionFor(colorName: String): NodeAction? =
    swatchActions[colorName]
        ?: actionId.takeIf { it.isNotBlank() }?.let { NodeAction(it, contextBindings) }

/**
 * True when the swatch named [colorName] must render non-interactive: either it is the currently
 * selected color ([selectedColor], the re-tap guard so re-tapping never re-fires) or it resolves to
 * no action at all. Renderers call this to decide whether to wire a tap.
 */
fun UiNode.ColorSwatchPicker.isSwatchInert(colorName: String, selectedColor: String?): Boolean =
    colorName == selectedColor || actionFor(colorName) == null
