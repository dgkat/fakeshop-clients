//
//  ProductListViewModel.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 3/11/25.
//
import Foundation
import ComposeApp
import Combine

@MainActor
class ProductListViewModel: ObservableObject {
    @Published var state: ProductListState
    
    private let viewStore: ProductListViewStore
    private let scope: Kotlinx_coroutines_coreCoroutineScope
    private var stateObservationTask: Task<Void, Never>?
    
    init() {
        self.scope = ScopeHelperKt.createMainScope()

        self.viewStore = KoinHelper.shared.iosHelper.getProductListViewStore(scope: scope)

        self.state = viewStore.productListState.value

        observeState()
    }
    
    private func observeState() {
        stateObservationTask = Task { @MainActor [weak self] in
            guard let stateFlow = self?.viewStore.productListState else { return }
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
    
    func loadProducts() {
        Task {
            do {
                try await viewStore.loadCategories()
            } catch {
                print("Error loading products: \(error)")
            }
        }
    }

    func toggleFavorite(productId: String) {
        viewStore.toggleFavorite(productId: productId)
    }

    deinit {
        ScopeHelperKt.cancelScope(scope: scope)
        stateObservationTask?.cancel()
    }
}
