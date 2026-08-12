//
//  SearchViewmodel.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 3/11/25.
//

import Foundation
import ComposeApp

@MainActor
class SearchViewModel: ObservableObject {
    @Published var state: SearchState
    
    private let viewStore: SearchViewStore
    private let scope: Kotlinx_coroutines_coreCoroutineScope
    private var stateObservationTask: Task<Void, Never>?
    
    init() {
        self.scope = ScopeHelperKt.createMainScope()

        self.viewStore = KoinHelper.shared.iosHelper.getSearchViewStore(scope: scope)

        self.state = viewStore.searchState.value

        observeState()
    }
    
    private func observeState() {
        stateObservationTask = Task { @MainActor [weak self] in
            guard let stateFlow = self?.viewStore.searchState else { return }
            do {
                for try await newState in stateFlow {
                    guard let self else { return }
                    self.state = newState
                }
            } catch {
                print("Error observing state: \(error)")
            }
        }
    }
    
    func onEvent(_ event: SearchEvent) {
        viewStore.onEvent(event: event)
    }
    
    deinit {
        ScopeHelperKt.cancelScope(scope: scope)
        stateObservationTask?.cancel()
    }
    
}
