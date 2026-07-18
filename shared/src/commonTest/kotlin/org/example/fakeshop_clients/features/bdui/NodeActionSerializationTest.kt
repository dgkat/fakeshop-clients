package org.example.fakeshop_clients.features.bdui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.example.fakeshop_clients.features.bdui.domain.models.NodeAction
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode

class NodeActionSerializationTest {

    @Test
    fun onTapDeserializesOnLeafNode() {
        val node = BduiJson.decodeFromString<UiNode>(
            """
            { "type": "Image", "bind": "socksImage",
              "onTap": {
                "actionId": "navigate",
                "contextBindings": { "url": "/product/socks-123" }
              } }
            """
        )

        val image = assertIs<UiNode.Image>(node)
        assertEquals(
            NodeAction(
                actionId = BduiConstants.NAVIGATE_ACTION_ID,
                contextBindings = mapOf(BduiConstants.URL_KEY to "/product/socks-123")
            ),
            image.onTap
        )
    }

    @Test
    fun onTapDeserializesOnContainerWithReplaceStackFlag() {
        val node = BduiJson.decodeFromString<UiNode>(
            """
            { "type": "Column",
              "onTap": {
                "actionId": "navigate",
                "contextBindings": { "url": "/favorites", "replace": "true" }
              },
              "children": [ { "type": "Text", "bind": "title" } ] }
            """
        )

        val column = assertIs<UiNode.Column>(node)
        assertEquals("true", column.onTap?.contextBindings?.get(BduiConstants.REPLACE_STACK_KEY))
        assertNull((column.children.single() as UiNode.Text).onTap)
    }

    @Test
    fun absentOnTapDefaultsToNull() {
        val node = BduiJson.decodeFromString<UiNode>("""{ "type": "Text", "bind": "title" }""")
        assertNull((node as UiNode.Text).onTap)
    }

    @Test
    fun contextBindingsDefaultsToEmptyMap() {
        val node = BduiJson.decodeFromString<UiNode>(
            """{ "type": "Text", "onTap": { "actionId": "navigate" } }"""
        )
        assertEquals(emptyMap(), (node as UiNode.Text).onTap?.contextBindings)
    }

    @Test
    fun navigateLiteralKeysAreSharedAcrossPlatforms() {
        // All platform renderers read this set to decide literal-vs-bind resolution;
        // the navigate keys must be in it or `url`/`replace` would be resolved as bind paths.
        assertEquals(
            setOf(
                BduiConstants.TARGET_SLOT_ID_KEY,
                BduiConstants.URL_KEY,
                BduiConstants.REPLACE_STACK_KEY
            ),
            BduiConstants.LITERAL_CONTEXT_KEYS
        )
    }
}
