//
//  SectionView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct SectionView: View {
    let node: UiNodeSection
    let data: BindData
    let onAction: BduiActionHandler

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let title = node.title, !title.isEmpty {
                Text(title)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(FakeShopColors.onSurfaceVariant)
                    .padding(.bottom, 2)
            }
            ForEach(Array(node.children.enumerated()), id: \.offset) { _, child in
                BduiNodeView(node: child, data: data, onAction: onAction)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
