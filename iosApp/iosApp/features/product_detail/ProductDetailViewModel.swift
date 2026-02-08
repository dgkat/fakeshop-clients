//
//  ProductDetailViewModel.swift
//  iosApp
//
//  Created by Dimitrios Katoudis
//
import Foundation
import ComposeApp
import Combine

@MainActor
class ProductDetailViewModel: ObservableObject {
    @Published var state: ProductDetailState

    private let viewStore: ProductDetailViewStore
    private let scope: Kotlinx_coroutines_coreCoroutineScope
    private var stateObservationTask: Task<Void, Never>?

    init() {
        self.scope = ScopeHelperKt.createMainScope()

        self.viewStore = KoinHelper.shared.iosHelper.getProductDetailViewStore(scope: scope)

        self.state = viewStore.state.value

        observeState()
    }

    private func observeState() {
        stateObservationTask = Task { @MainActor in
            do {
                for try await newState in viewStore.state {
                    self.state = newState
                }
            } catch {
                print("Error observing state: \(error)")
            }
        }
    }

    func onEvent(_ event: ProductDetailEvent) {
        viewStore.onEvent(event: event)
    }

    deinit {
        ScopeHelperKt.cancelScope(scope: scope)
        stateObservationTask?.cancel()
    }
}
