//
//  ColumnRowView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct ColumnView: View {
    let node: UiNodeColumn
    let data: BindData
    let onAction: BduiActionHandler

    var body: some View {
        VStack(alignment: .leading, spacing: spacing(node.spacing)) {
            ForEach(Array(node.children.enumerated()), id: \.offset) { _, child in
                BduiNodeView(node: child, data: data, onAction: onAction)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct RowView: View {
    let node: UiNodeRow
    let data: BindData
    let onAction: BduiActionHandler

    var body: some View {
        HStack(alignment: .top, spacing: spacing(node.spacing)) {
            ForEach(Array(node.children.enumerated()), id: \.offset) { _, child in
                let weight = child.weight?.floatValue
                if let w = weight {
                    BduiNodeView(node: child, data: data, onAction: onAction)
                        .frame(maxWidth: .infinity)
                        .layoutPriority(Double(w))
                } else {
                    BduiNodeView(node: child, data: data, onAction: onAction)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
