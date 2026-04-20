import SwiftUI
import UIKit
import UserNotifications
import ComposeApp

struct NotificationPermissionBanner: View {
    let permissionStatus: NotificationPermissionStatus
    let showBanner: Bool
    let onPermissionResult: (Bool) -> Void

    var body: some View {
        Group {
            switch permissionStatus {
            case .notDetermined:
                if showBanner {
                    enableBanner
                }
            case .denied:
                deniedBanner
            default:
                EmptyView()
            }
        }
    }

    private var enableBanner: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(String(localized: "notification_permission_title"))
                .font(.subheadline)
                .fontWeight(.semibold)

            Text(String(localized: "notification_permission_message"))
                .font(.caption)
                .foregroundColor(FakeShopColors.onSurfaceVariant)

            Button(action: requestPermission) {
                Text(String(localized: "notification_permission_enable"))
                    .font(.caption)
                    .fontWeight(.medium)
                    .foregroundColor(FakeShopColors.onPrimary)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(FakeShopColors.primary)
                    .cornerRadius(8)
            }
            .padding(.top, 4)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(FakeShopColors.primaryContainer)
        .cornerRadius(12)
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }

    private var deniedBanner: some View {
        HStack {
            Text(String(localized: "notification_permission_denied"))
                .font(.caption)
                .foregroundColor(FakeShopColors.onSurfaceVariant)

            Spacer()

            Button(action: openSettings) {
                Text(String(localized: "notification_permission_settings"))
                    .font(.caption)
                    .fontWeight(.medium)
            }
        }
        .padding(12)
        .background(FakeShopColors.surfaceVariant)
        .cornerRadius(8)
        .padding(.horizontal, 16)
        .padding(.vertical, 4)
    }

    private func requestPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, _ in
            Task { @MainActor in
                onPermissionResult(granted)
            }
        }
    }

    private func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
    }
}
