import SwiftUI

@main
struct iOSApp: App {
    
    init() {
        _ = KoinHelper.shared
        
        print("App initialized with Koin")
    }
    
    var body: some Scene {
        WindowGroup {
            HomeView()
        }
    }
}
