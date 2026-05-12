//
//  ColorSwatchPickerView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct ColorSwatchPickerView: View {
    let node: UiNodeColorSwatchPicker
    let data: BindData
    @State private var selected: String? = nil

    var body: some View {
        let entries = (node.bind.map { data.resolveColorList(path: $0) } ?? nil) ?? []
        HStack(spacing: 8) {
            ForEach(Array(entries.enumerated()), id: \.offset) { _, entry in
                Button(action: { selected = entry.name }) {
                    Circle()
                        .fill(color(from: entry.hex))
                        .frame(width: 28, height: 28)
                        .overlay(
                            Circle().stroke(
                                selected == entry.name ? FakeShopColors.primary : FakeShopColors.outline,
                                lineWidth: selected == entry.name ? 2 : 1
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
