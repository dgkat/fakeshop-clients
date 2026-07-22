import SwiftUI
import ComposeApp

struct ProductRoute: Hashable {
    let productId: String
    private let token = UUID()

    init(_ productId: String) {
        self.productId = productId
    }
}

class NavigationRouter: ObservableObject {
    static let shared = NavigationRouter()

    @Published private(set) var requestToken = UUID()
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
