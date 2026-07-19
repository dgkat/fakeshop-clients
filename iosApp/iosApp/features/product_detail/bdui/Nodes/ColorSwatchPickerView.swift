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

        if !entries.isEmpty {
            HStack(spacing: 8) {
                ForEach(Array(entries.enumerated()), id: \.offset) { _, entry in
                    let isSelected = selectedName == entry.name
                    // Re-tap guard: the selected swatch (or one with no resolved action) is inert —
                    // render it without a Button so re-tapping the current color never re-fires.
                    if let action = resolvedAction(for: entry.name, isSelected: isSelected) {
                        Button(action: {
                            let ctx = buildActionContext(bindings: action.bindings, data: data, extra: ["color": entry.name])
                            onAction(action.actionId, ctx)
                        }) {
                            swatch(entry: entry, isSelected: isSelected)
                        }
                        .buttonStyle(PlainButtonStyle())
                        .accessibilityLabel(entry.name)
                    } else {
                        swatch(entry: entry, isSelected: isSelected)
                            .accessibilityLabel(entry.name)
                    }
                }
            }
        }
    }

    private func swatch(entry: ColorEntry, isSelected: Bool) -> some View {
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

    /// Effective per-swatch action: the `swatchActions` override for this color, else the node's
    /// default (`actionId` + `contextBindings`). Returns `nil` when the swatch is the selected one
    /// (re-tap guard) or resolves to no action — mirrors the shared `isSwatchInert` / `actionFor`.
    private func resolvedAction(for colorName: String, isSelected: Bool) -> (actionId: String, bindings: [String: String])? {
        if isSelected { return nil }
        if let override = node.swatchActions[colorName] {
            return (override.actionId, override.contextBindings)
        }
        guard !node.actionId.isEmpty else { return nil }
        return (node.actionId, node.contextBindings)
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
