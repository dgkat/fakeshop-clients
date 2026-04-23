import SwiftUI

struct BootstrapFailedView: View {
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text(String(localized: "bootstrap_failed_body"))
                .multilineTextAlignment(.center)
                .foregroundColor(FakeShopColors.onSurfaceVariant)
                .padding(.horizontal, 32)
            Button(String(localized: "bootstrap_failed_retry")) {
                onRetry()
            }
            .buttonStyle(.borderedProminent)
            .tint(FakeShopColors.primary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(FakeShopColors.surface)
        .ignoresSafeArea()
    }
}
