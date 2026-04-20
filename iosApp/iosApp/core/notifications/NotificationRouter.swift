import SwiftUI

class NotificationRouter: ObservableObject {
    static let shared = NotificationRouter()

    @Published var productIdToNavigate: String?

    private init() {}

    func navigateToProduct(_ productId: String) {
        productIdToNavigate = productId
    }

    func clearNavigation() {
        productIdToNavigate = nil
    }
}
