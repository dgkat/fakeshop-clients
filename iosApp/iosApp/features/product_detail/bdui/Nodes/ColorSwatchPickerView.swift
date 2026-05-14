//
//  ColorSwatchPickerView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct ColorSwatchPickerView: View {
    let node: UiNodeColorSwatchPicker
    let data: BindData
    let onAction: BduiActionHandler

    var body: some View {
        let entries = (node.bind.map { data.resolveColorList(path: $0) } ?? nil) ?? []
        let selectedName = data.resolveString(path: "data.selectedColor")

        HStack(spacing: 8) {
            ForEach(Array(entries.enumerated()), id: \.offset) { _, entry in
                let isSelected = selectedName == entry.name
                Button(action: {
                    guard !node.actionId.isEmpty else { return }
                    let ctx = buildActionContext(bindings: node.contextBindings, data: data, extra: ["color": entry.name])
                    onAction(node.actionId, ctx)
                }) {
                    Circle()
                        .fill(color(from: entry.hex))
                        .frame(width: 28, height: 28)
                        .overlay(
                            Circle().stroke(
                                isSelected ? FakeShopColors.primary : FakeShopColors.outline,
                                lineWidth: isSelected ? 2 : 1
                            )
                        )
                }
                .buttonStyle(PlainButtonStyle())
                .accessibilityLabel(entry.name)
            }
        }
    }

    private func color(from hex: String) -> Color {
        let stripped = hex.hasPrefix("#") ? String(hex.dropFirst()) : hex
        guard let value = UInt64(stripped, radix: 16) else { return .gray }
        switch stripped.count {
        case 6:
            let r = Double((value >> 16) & 0xFF) / 255
            let g = Double((value >> 8) & 0xFF) / 255
            let b = Double(value & 0xFF) / 255
            return Color(red: r, green: g, blue: b)
        case 8:
            let a = Double((value >> 24) & 0xFF) / 255
            let r = Double((value >> 16) & 0xFF) / 255
            let g = Double((value >> 8) & 0xFF) / 255
            let b = Double(value & 0xFF) / 255
            return Color(red: r, green: g, blue: b, opacity: a)
        default:
            return .gray
        }
    }
}
