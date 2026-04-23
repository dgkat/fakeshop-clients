import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        _ = KoinHelper.shared

        print("App initialized with Koin")

        bootstrapSession()
    }

    var body: some Scene {
        WindowGroup {
            AppRootView()
        }
    }

    private func bootstrapSession() {
        let helper = KoinHelper.shared.iosHelper
        let bootstrapper = helper.getSessionBootstrapper()
        let notificationsService = helper.getNotificationsService()

        Task {
            try? await bootstrapper.bootstrap()

            if helper.isSessionRealUser() {
                if notificationsService.getPermissionStatus() == NotificationPermissionStatus.granted {
                    notificationsService.registerDeviceAfterAuth()
                }
            }
        }
    }
}

struct AppRootView: View {
    @StateObject private var viewModel = AppRootViewModel()

    var body: some View {
        switch viewModel.sessionState {
        case .loading:
            FakeShopColors.surface.ignoresSafeArea()
        case .ready:
            MainTabView()
        case .bootstrapFailed:
            BootstrapFailedView(onRetry: { viewModel.retry() })
        }
    }
}
