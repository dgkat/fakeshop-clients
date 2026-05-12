//
//  SizeSelectorView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct SizeSelectorView: View {
    let node: UiNodeSizeSelector
    let data: BindData
    @State private var selected: String? = nil

    var body: some View {
        let sizes = (node.bind.map { data.resolveStringList(path: $0) } ?? nil) ?? []
        FlowHStack(spacing: 8) {
            ForEach(Array(sizes.enumerated()), id: \.offset) { _, size in
                Button(action: { selected = size }) {
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

// Minimal flow-wrap HStack for chips.
struct FlowHStack<Content: View>: View {
    let spacing: CGFloat
    let content: () -> Content

    init(spacing: CGFloat = 8, @ViewBuilder content: @escaping () -> Content) {
        self.spacing = spacing
        self.content = content
    }

    var body: some View {
        // SwiftUI's HStack wraps via Layout in iOS 16+; fall back to a simple horizontal stack.
        if #available(iOS 16.0, *) {
            ViewThatFits(in: .horizontal) {
                HStack(spacing: spacing) { content() }
                // Wrap into multiple rows by using LazyVGrid as a fallback isn't trivial here;
                // rely on horizontal scroll if it overflows.
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
