//
//  BduiRenderer.swift
//  iosApp
//

import SwiftUI
import ComposeApp

typealias BduiActionHandler = (ActionRef) -> Void

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
        case .sizeSelector(let n): SizeSelectorView(node: n, data: data)
        case .colorSwatchPicker(let n): ColorSwatchPickerView(node: n, data: data)
        case .button(let n): ButtonNodeView(node: n, onAction: onAction)
        case .spacer(let n): SpacerNodeView(node: n)
        case .divider: DividerNodeView()
        }
    }
}

private struct NodeLayoutModifier: ViewModifier {
    let node: UiNode

    func body(content: Content) -> some View {
        let widthFraction = node.widthFraction?.floatValue
        let alignment = node.alignment

        Group {
            if let w = widthFraction {
                GeometryReader { proxy in
                    aligned(content: content, alignment: alignment)
                        .frame(width: proxy.size.width * CGFloat(w))
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
