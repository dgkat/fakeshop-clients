package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.ButtonStyle
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.NodeAlignment
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.SpacerSize
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.Spacing
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.TextStyle

@Serializable
data class NodeAction(
    val actionId: String,
    val contextBindings: Map<String, String> = emptyMap()
)

@Serializable
sealed interface UiNode {
    val weight: Float?
    val widthFraction: Float?
    val alignment: NodeAlignment?

    /**
     * Optional replace/anchor slot id the CMS can author on any node except [Divider].
     * Hoisted onto the interface so `slotId` is uniformly reachable (no hand-maintained `when`)
     * and the compiler forces every new node type to declare it.
     */
    val slotId: String?

    /** Optional tap action. Never present on Divider or the interactive nodes (BE rejects it there). */
    val onTap: NodeAction?

    @Serializable
    @SerialName("Column")
    data class Column(
        val spacing: Spacing = Spacing.md,
        val children: List<UiNode> = emptyList(),
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("Row")
    data class Row(
        val spacing: Spacing = Spacing.md,
        val children: List<UiNode> = emptyList(),
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("Section")
    data class Section(
        val title: String? = null,
        val children: List<UiNode> = emptyList(),
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("Text")
    data class Text(
        val style: TextStyle = TextStyle.body,
        val bind: String? = null,
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("Image")
    data class Image(
        val bind: String? = null,
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("ImageGallery")
    data class ImageGallery(
        val bind: String? = null,
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("PriceBlock")
    data class PriceBlock(
        val bind: String? = null,
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("SpecTable")
    data class SpecTable(
        val bind: String? = null,
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("SizeSelector")
    data class SizeSelector(
        val bind: String? = null,
        val actionId: String = "",
        val contextBindings: Map<String, String> = emptyMap(),
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("ColorSwatchPicker")
    data class ColorSwatchPicker(
        val bind: String? = null,
        val actionId: String = "",
        val contextBindings: Map<String, String> = emptyMap(),
        val swatchActions: Map<String, NodeAction> = emptyMap(),
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("Button")
    data class Button(
        val label: String = "",
        val style: ButtonStyle = ButtonStyle.primary,
        val actionId: String = "",
        val contextBindings: Map<String, String> = emptyMap(),
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("Spacer")
    data class Spacer(
        val size: SpacerSize = SpacerSize.md,
        override val slotId: String? = null,
        override val weight: Float? = null,
        override val widthFraction: Float? = null,
        override val alignment: NodeAlignment? = null,
        override val onTap: NodeAction? = null
    ) : UiNode

    @Serializable
    @SerialName("Divider")
    data object Divider : UiNode {
        override val weight: Float? = null
        override val widthFraction: Float? = null
        override val alignment: NodeAlignment? = null
        override val slotId: String? = null
        override val onTap: NodeAction? = null
    }
}
