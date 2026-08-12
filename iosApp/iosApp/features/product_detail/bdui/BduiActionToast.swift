//
//  BduiEffectHandler.swift
//  iosApp
//

import SwiftUI
import ComposeApp

final class BduiToastState: ObservableObject {
    @Published var message: String? = nil

    func show(_ text: String) {
        DispatchQueue.main.async { [weak self] in
            self?.message = text
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) { [weak self] in
                if self?.message == text { self?.message = nil }
            }
        }
    }
}

struct BduiToastOverlay: View {
    @ObservedObject var state: BduiToastState

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
