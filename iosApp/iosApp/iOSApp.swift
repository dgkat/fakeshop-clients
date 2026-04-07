import SwiftUI

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        _ = KoinHelper.shared

        print("App initialized with Koin")
    }

    var body: some Scene {
        WindowGroup {
            MainTabView()
        }
    }
}
