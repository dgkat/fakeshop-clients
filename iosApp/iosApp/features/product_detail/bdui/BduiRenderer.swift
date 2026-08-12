//
//  BduiRenderer.swift
//  iosApp
//

import SwiftUI
import ComposeApp

/// Action context is an OPAQUE Kotlin `ActionContext` (not a Swift `Dictionary`) on purpose: it must
/// never be bridged to/from `[String: JsonElement]`, or the round trip yields a dangling Kotlin `Map`
/// that crashes on `Map#get`. The wrapped `JsonObject` is only ever unwrapped in Kotlin
/// (`ProductDetailViewStore.dispatchBduiAction`). See `ActionContext` in shared.
typealias BduiActionHandler = (String, ActionContext) -> Void

/// Resolves contextBindings from BindData and merges extra literal values into an opaque ActionContext.
/// Literal keys (targetSlotId, url, replace — shared `LITERAL_CONTEXT_KEYS`) are authored verbatim,
/// not as bind paths, so they are pulled out of the path-resolved bindings and passed through as-is.
func buildActionContext(bindings: [String: String], data: BindData, extra: [String: String] = [:]) -> ActionContext {
    let literalKeys = BduiConstants.shared.LITERAL_CONTEXT_KEYS
    var pathBindings = bindings
    var literals = extra
    for key in literalKeys {
        if let value = pathBindings.removeValue(forKey: key) {
            literals[key] = value
        }
    }
    return data.resolveActionContext(bindings: pathBindings, extra: literals)
}

/// Replaced slots in scope for the current subtree, keyed by `targetSlotId`. Empty by default and
/// re-scoped to empty inside a replacement so the standalone layout never re-triggers a swap.
private struct BduiReplacedSlotsKey: EnvironmentKey {
    static let defaultValue: [String: ResolvedReplace] = [:]
}

extension EnvironmentValues {
    var bduiReplacedSlots: [String: ResolvedReplace] {
        get { self[BduiReplacedSlotsKey.self] }
        set { self[BduiReplacedSlotsKey.self] = newValue }
    }
}

struct BduiNodeView: View {
    let node: UiNode
    let data: BindData
    let onAction: BduiActionHandler
    @Environment(\.bduiReplacedSlots) private var replacedSlots

    var body: some View {
        if let replacement = BduiReplaceResolver.shared.replacementFor(node: node, replacedSlots: replacedSlots) {
            // Standalone-scope replace: render the replacement layout against its own `values`
            // instead of the original subtree + product bindData. Clear replacedSlots for the
            // subtree so the standalone layout can't recursively re-trigger a swap.
            //
            // AnyView is REQUIRED here, not cosmetic: this branch embeds `BduiNodeView` directly
            // (not via a ForEach), so without erasure SwiftUI's static `_viewListCount` would
            // recurse `_ConditionalContent` → BduiNodeView → _ConditionalContent → … forever and
            // hang the render thread (the body never commits, so the loader stays on screen).
            // AnyView gives this branch a dynamic count and terminates the static recursion. The
            // normal subtree (else branch) is safe because it only recurses through ForEach.
            AnyView(
                BduiNodeView(
                    node: replacement.node,
                    data: replacement.bindData,
                    onAction: onAction
                )
                .environment(\.bduiReplacedSlots, [:])
            )
        } else {
            renderedBody
                .modifier(NodeTapModifier(node: node, data: data, onAction: onAction))
                .modifier(NodeLayoutModifier(node: node))
        }
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

/// Dispatches a node's optional `onTap` action. Applied inside NodeLayoutModifier so the tap
/// area matches the node's visual bounds. Children with their own tap handling (e.g. Button)
/// consume taps before this gesture, giving the innermost-handler-wins rule for free.
private struct NodeTapModifier: ViewModifier {
    let node: UiNode
    let data: BindData
    let onAction: BduiActionHandler

    func body(content: Content) -> some View {
        if let onTap = node.onTap {
            content
                .contentShape(Rectangle())
                .onTapGesture {
                    onAction(onTap.actionId, buildActionContext(bindings: onTap.contextBindings, data: data))
                }
        } else {
            content
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
