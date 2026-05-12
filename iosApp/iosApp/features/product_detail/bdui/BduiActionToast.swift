//
//  BduiActionToast.swift
//  iosApp
//
//  v1: action handler is a stub — log + transient banner. Full action protocol
//  (BE-driven actionId → response → patch UI) is a future TODO.
//

import SwiftUI
import ComposeApp

final class BduiActionToastState: ObservableObject {
    @Published var message: String? = nil

    func makeHandler() -> BduiActionHandler {
        return { [weak self] action in
            let label: String
            if let target = action.target {
                label = "\(action.type): \(target)"
            } else {
                label = action.type
            }
            print("[BduiAction] \(label)")
            self?.show(label)
        }
    }

    private func show(_ text: String) {
        DispatchQueue.main.async { [weak self] in
            self?.message = text
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) { [weak self] in
                if self?.message == text { self?.message = nil }
            }
        }
    }
}

struct BduiActionToastOverlay: View {
    @ObservedObject var state: BduiActionToastState

    var body: some View {
        VStack {
            Spacer()
            if let msg = state.message {
                Text(msg)
                    .font(.system(size: 13, weight: .medium))
                    .padding(.vertical, 10)
                    .padding(.horizontal, 16)
                    .background(FakeShopColors.onSurface.opacity(0.85))
                    .foregroundColor(FakeShopColors.surface)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .padding(.bottom, 80)
                    .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.2), value: state.message)
        .allowsHitTesting(false)
    }
}
