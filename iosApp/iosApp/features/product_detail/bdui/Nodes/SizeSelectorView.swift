//
//  SizeSelectorView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct SizeSelectorView: View {
    let node: UiNodeSizeSelector
    let data: BindData
    let onAction: BduiActionHandler

    var body: some View {
        let sizes = (node.bind.map { data.resolveStringList(path: $0) } ?? nil) ?? []
        let selected = data.resolveString(path: "data.selectedSize")

        if !sizes.isEmpty {
            FlowHStack(spacing: 8) {
                ForEach(Array(sizes.enumerated()), id: \.offset) { _, size in
                    Button(action: {
                        guard !node.actionId.isEmpty else { return }
                        let ctx = buildActionContext(bindings: node.contextBindings, data: data, extra: ["size": size])
                        onAction(node.actionId, ctx)
                    }) {
                        Text(size)
                            .font(.system(size: 13))
                            .padding(.vertical, 6)
                            .padding(.horizontal, 14)
                            .background(selected == size ? FakeShopColors.primary : Color.clear)
                            .foregroundColor(selected == size ? FakeShopColors.onPrimary : FakeShopColors.onSurface)
                            .overlay(
                                RoundedRectangle(cornerRadius: 999)
                                    .stroke(selected == size ? FakeShopColors.primary : FakeShopColors.outline, lineWidth: 1)
                            )
                            .clipShape(RoundedRectangle(cornerRadius: 999))
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
        }
    }
}

// Minimal flow-wrap HStack for chips.
struct FlowHStack<Content: View>: View {
    let spacing: CGFloat
    let content: () -> Content

    init(spacing: CGFloat = 8, @ViewBuilder content: @escaping () -> Content) {
        self.spacing = spacing
        self.content = content
    }

    var body: some View {
        if #available(iOS 16.0, *) {
            ViewThatFits(in: .horizontal) {
                HStack(spacing: spacing) { content() }
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: spacing) { content() }
                }
            }
        } else {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: spacing) { content() }
            }
        }
    }
}
