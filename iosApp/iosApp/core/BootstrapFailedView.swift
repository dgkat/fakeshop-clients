import SwiftUI

struct BootstrapFailedView: View {
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text("Can't reach the server.\nCheck your connection and try again.")
                .multilineTextAlignment(.center)
                .foregroundColor(FakeShopColors.onSurfaceVariant)
                .padding(.horizontal, 32)
            Button("Retry") {
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
