import SwiftUI
import ComposeApp

enum AppSessionState {
    case loading
    case ready
    case bootstrapFailed
}

@MainActor
class AppRootViewModel: ObservableObject {
    @Published var sessionState: AppSessionState = .loading

    private let sessionObserver: SessionObserver
    private var observationTask: Task<Void, Never>?

    init() {
        self.sessionObserver = KoinHelper.shared.iosHelper.getSessionObserver()

        // Sync initial value immediately — StateFlow always has a current value
        let initial = sessionObserver.state.value
        if initial is SessionStateAuthenticated {
            sessionState = .ready
        } else if initial is SessionStateBootstrapFailed {
            sessionState = .bootstrapFailed
        }

        startObserving()
    }

    private func startObserving() {
        observationTask = Task { @MainActor in
            do {
                for try await newState in sessionObserver.state {
                    if newState is SessionStateAuthenticated {
                        self.sessionState = .ready
                    } else if newState is SessionStateBootstrapFailed {
                        self.sessionState = .bootstrapFailed
                    } else {
                        self.sessionState = .loading
                    }
                }
            } catch {
                print("Error observing session state: \(error)")
            }
        }
    }

    func retry() {
        let bootstrapper = KoinHelper.shared.iosHelper.getSessionBootstrapper()
        Task {
            try? await bootstrapper.bootstrap()
        }
    }

    deinit {
        observationTask?.cancel()
    }
}
