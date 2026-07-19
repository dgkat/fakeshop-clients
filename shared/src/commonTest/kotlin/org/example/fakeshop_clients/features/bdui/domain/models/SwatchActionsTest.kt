package org.example.fakeshop_clients.features.bdui.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Covers the shared per-swatch action resolution ([actionFor]) and the re-tap guard
 * ([isSwatchInert]) that every renderer (Android / iOS / web SSR) relies on.
 */
class SwatchActionsTest {

    private fun picker(
        actionId: String = "selectVariant",
        contextBindings: Map<String, String> = emptyMap(),
        swatchActions: Map<String, NodeAction> = emptyMap()
    ) = UiNode.ColorSwatchPicker(
        bind = "data.colors",
        actionId = actionId,
        contextBindings = contextBindings,
        swatchActions = swatchActions
    )

    @Test
    fun overrideWinsOverDefault() {
        val node = picker(
            actionId = "selectVariant",
            swatchActions = mapOf(
                "Blue" to NodeAction("navigate", mapOf("url" to "/product/shirt-blue"))
            )
        )

        val action = node.actionFor("Blue")

        assertEquals("navigate", action?.actionId)
        assertEquals("/product/shirt-blue", action?.contextBindings?.get("url"))
    }

    @Test
    fun replaceOverrideResolves() {
        val node = picker(
            swatchActions = mapOf(
                "Red" to NodeAction("replace", mapOf("targetSlotId" to "variant-panel"))
            )
        )

        val action = node.actionFor("Red")

        assertEquals("replace", action?.actionId)
        assertEquals("variant-panel", action?.contextBindings?.get("targetSlotId"))
    }

    @Test
    fun unmappedColorFallsBackToNodeDefault() {
        val node = picker(
            actionId = "selectVariant",
            contextBindings = mapOf("k" to "v"),
            swatchActions = mapOf("Blue" to NodeAction("navigate", mapOf("url" to "/favorites")))
        )

        val action = node.actionFor("Green")

        assertEquals("selectVariant", action?.actionId)
        assertEquals("v", action?.contextBindings?.get("k"))
    }

    @Test
    fun blankDefaultAndNoOverrideResolvesToNull() {
        val node = picker(actionId = "", swatchActions = emptyMap())

        assertNull(node.actionFor("Green"))
    }

    @Test
    fun selectedColorIsInertEvenWithAnAction() {
        val node = picker(
            swatchActions = mapOf("Red" to NodeAction("navigate", mapOf("url" to "/product/shirt-red")))
        )

        assertTrue(node.isSwatchInert("Red", selectedColor = "Red"))
        assertFalse(node.isSwatchInert("Blue", selectedColor = "Red"))
    }

    @Test
    fun colorWithNoActionIsInertRegardlessOfSelection() {
        val node = picker(actionId = "", swatchActions = emptyMap())

        assertTrue(node.isSwatchInert("Green", selectedColor = "Red"))
    }
}
