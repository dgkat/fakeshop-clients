//
//  BduiRenderer.swift
//  iosApp
//

import SwiftUI
import ComposeApp

typealias JsonObject = [String: Kotlinx_serialization_jsonJsonElement]
typealias BduiActionHandler = (String, JsonObject) -> Void

/// Resolves contextBindings from BindData and merges extra literal values into a JsonObject.
func buildActionContext(bindings: [String: String], data: BindData, extra: [String: String] = [:]) -> JsonObject {
    return data.resolveActionContext(bindings: bindings, extra: extra)
}

struct BduiNodeView: View {
    let node: UiNode
    let data: BindData
    let onAction: BduiActionHandler

    var body: some View {
        renderedBody
            .modifier(NodeLayoutModifier(node: node))
    }

    @ViewBuilder
    private var renderedBody: some View {
        switch onEnum(of: node) {
        case .column(let n): ColumnView(node: n, data: data, onAction: onAction)
        case .row(let n): RowView(node: n, data: data, onAction: onAction)
        case .section(let n): SectionView(node: n, data: data, onAction: onAction)
        case .text(let n): TextNodeView(node: n, data: data)
        case .image(let n): ImageNodeView(node: n, data: data)
        case .imageGallery(let n): ImageGalleryNodeView(node: n, data: data)
        case .priceBlock(let n): PriceBlockView(node: n, data: data)
        case .specTable(let n): SpecTableView(node: n, data: data)
        case .sizeSelector(let n): SizeSelectorView(node: n, data: data, onAction: onAction)
        case .colorSwatchPicker(let n): ColorSwatchPickerView(node: n, data: data, onAction: onAction)
        case .button(let n): ButtonNodeView(node: n, data: data, onAction: onAction)
        case .spacer(let n): SpacerNodeView(node: n)
        case .divider: DividerNodeView()
        }
    }
}

/// Single-child layout that allocates `fraction * parentWidth` to its child and positions it
/// according to `alignment` within the full parent width. Unlike GeometryReader it correctly
/// reports the child's actual height to the parent, so ScrollView contentSize is never underestimated.
private struct FractionalWidthLayout: Layout {
    let fraction: Float
    let alignment: NodeAlignment?

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        guard let totalWidth = proposal.width, totalWidth > 0 else {
            return subviews.first?.sizeThatFits(.unspecified) ?? .zero
        }
        let childWidth = totalWidth * CGFloat(fraction)
        let childHeight = subviews.first?.sizeThatFits(
            ProposedViewSize(width: childWidth, height: proposal.height)
        ).height ?? 0
        return CGSize(width: totalWidth, height: childHeight)
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        guard let subview = subviews.first else { return }
        let childWidth = bounds.width * CGFloat(fraction)
        let x: CGFloat
        switch alignment {
        case .center?: x = bounds.minX + (bounds.width - childWidth) / 2
        case .end?:    x = bounds.maxX - childWidth
        default:       x = bounds.minX
        }
        subview.place(
            at: CGPoint(x: x, y: bounds.minY),
            proposal: ProposedViewSize(width: childWidth, height: proposal.height)
        )
    }
}

private struct NodeLayoutModifier: ViewModifier {
    let node: UiNode

    func body(content: Content) -> some View {
        let widthFraction = node.widthFraction?.floatValue
        let alignment = node.alignment

        Group {
            if let w = widthFraction {
                FractionalWidthLayout(fraction: w, alignment: alignment) {
                    content
                }
            } else {
                aligned(content: content, alignment: alignment)
            }
        }
    }

    @ViewBuilder
    private func aligned(content: Content, alignment: NodeAlignment?) -> some View {
        switch alignment {
        case .start?:
            HStack { content; Spacer(minLength: 0) }
        case .center?:
            HStack { Spacer(minLength: 0); content; Spacer(minLength: 0) }
        case .end?:
            HStack { Spacer(minLength: 0); content }
        default:
            content
        }
    }
}

// MARK: - Token helpers

func spacing(_ s: Spacing) -> CGFloat {
    switch s {
    case .sm: return 4
    case .md: return 8
    case .lg: return 16
    case .xl: return 24
    default: return 8
    }
}

func spacerHeight(_ s: SpacerSize) -> CGFloat {
    switch s {
    case .sm: return 8
    case .md: return 16
    case .lg: return 24
    case .xl: return 40
    default: return 16
    }
}

func bduiFont(for style: TextStyle) -> Font {
    switch style {
    case .title: return .system(size: 24, weight: .semibold)
    case .subtitle: return .system(size: 18, weight: .medium)
    case .body: return .system(size: 14)
    case .caption: return .system(size: 12)
    default: return .body
    }
}

func textAlignment(for alignment: NodeAlignment?) -> TextAlignment {
    switch alignment {
    case .center?: return .center
    case .end?: return .trailing
    default: return .leading
    }
}
