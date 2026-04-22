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
            MainTabView()
        }
    }

    /// Mirrors Android's `FakeShopApp.bootstrapSession()`:
    /// seeds `SessionStore` from the backend on cold start so UI that observes
    /// session state reflects reality before the user opens the Profile screen.
    private func bootstrapSession() {
        let helper = KoinHelper.shared.iosHelper
        let profileService = helper.getProfileService()
        let sessionMutator = helper.getSessionMutator()

        // TODO(step-5): replace with SessionBootstrapper.
        Task {
            let result = try? await profileService.checkLoginStatus()
            let isLoggedIn = (result as? ResultSuccess<KotlinBoolean>)?.data?.boolValue ?? false
            if isLoggedIn {
                sessionMutator.setAuthenticated(role: Role.loggedUser)
            } else {
                sessionMutator.setAuthenticated(role: Role.guest)
            }
        }
    }
}
