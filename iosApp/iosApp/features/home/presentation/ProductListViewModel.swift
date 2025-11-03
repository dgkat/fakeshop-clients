//
//  ProductListViewModel.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 3/11/25.
//
import Foundation
import Shared
import Combine

@MainActor
class ProductListViewModel: ObservableObject {
    @Published var state: ProductListState
    
    private let viewStore: ProductListViewStore
    private var stateObservationTask: Task<Void, Never>?
    
    init() {
        // Get ViewStore from Koin
        //let koin = KoinHelper.shared.koin
        //self.viewStore = koin.get(objCClass: ProductListViewStore.self) as! ProductListViewStore
        
        self.viewStore = ProductListViewStore.companion.create()
        // Initialize with current state
        self.state = viewStore.productListState.value 
        
        // Start observing state changes
        observeState()
    }
    
    private func observeState() {
        stateObservationTask = Task { @MainActor in
            do {
                for try await newState in viewStore.productListState {
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
    
    deinit {
        stateObservationTask?.cancel()
    }
}
