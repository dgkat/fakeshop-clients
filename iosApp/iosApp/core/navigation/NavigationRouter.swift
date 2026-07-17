import SwiftUI
import ComposeApp

class NavigationRouter: ObservableObject {
    static let shared = NavigationRouter()

    private(set) var pendingRoute: AppRoute?
    private(set) var pendingReplace = false

    private init() {}

    func navigate(url: String, replace: Bool = false) {
        guard let route = AppRouteParser.shared.parse(input: url) else { return }
        navigate(to: route, replace: replace)
    }

    func navigate(to route: AppRoute, replace: Bool = false) {
        pendingRoute = route
        pendingReplace = replace
        requestToken = UUID()
    }

    func clear() {
        pendingRoute = nil
        pendingReplace = false
    }
}
