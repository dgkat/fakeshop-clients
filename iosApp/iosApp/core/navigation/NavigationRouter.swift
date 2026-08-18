import SwiftUI
import ComposeApp

/// A PDP push, carrying the originating-list attribution across the navigation hop — the list the
/// user tapped, not the screen they land on. `surface` stays a Kotlin enum the whole way rather
/// than a Swift string: a typo in a string literal is accepted silently by the backend and stored
/// as-is, whereas an enum is a compile error.
///
/// `token` keeps every push distinct, so navigating to a product already on the stack forms a new
/// entry instead of a duplicate hashable value that NavigationStack no-ops on. Equality/hash are
/// written by hand off that token, so the Kotlin enum needn't be Hashable.
struct ProductRoute: Hashable {
    let productId: String
    let surface: InteractionSurface
    let position: Int?
    private let token = UUID()

    init(
        _ productId: String,
        surface: InteractionSurface = InteractionSurface.productScreen,
        position: Int? = nil
    ) {
        self.productId = productId
        self.surface = surface
        self.position = position
    }

    static func == (lhs: ProductRoute, rhs: ProductRoute) -> Bool { lhs.token == rhs.token }

    func hash(into hasher: inout Hasher) { hasher.combine(token) }
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
