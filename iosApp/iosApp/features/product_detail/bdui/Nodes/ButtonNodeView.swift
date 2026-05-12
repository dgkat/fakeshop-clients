//
//  ButtonNodeView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct ButtonNodeView: View {
    let node: UiNodeButton
    let onAction: BduiActionHandler

    var body: some View {
        Button(action: { onAction(node.action) }) {
            Text(node.label)
                .font(.system(size: 14, weight: .medium))
                .padding(.vertical, 10)
                .padding(.horizontal, 20)
                .frame(maxWidth: sized ? nil : .infinity)
                .background(background)
                .foregroundColor(foreground)
                .overlay(
                    RoundedRectangle(cornerRadius: 8).stroke(borderColor, lineWidth: 1)
                )
                .clipShape(RoundedRectangle(cornerRadius: 8))
        }
        .buttonStyle(PlainButtonStyle())
    }

    private var sized: Bool { node.alignment != nil || node.widthFraction != nil }

    private var background: Color {
        switch node.style {
        case .primary: return FakeShopColors.primary
        default: return .clear
        }
    }

    private var foreground: Color {
        switch node.style {
        case .primary: return FakeShopColors.onPrimary
        default: return FakeShopColors.primary
        }
    }

    private var borderColor: Color {
        switch node.style {
        case .secondary: return FakeShopColors.primary
        default: return .clear
        }
    }
}
