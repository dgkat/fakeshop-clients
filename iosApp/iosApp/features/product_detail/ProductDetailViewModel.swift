//
//  ProductDetailViewModel.swift
//  iosApp
//
import Foundation
import ComposeApp
import Combine

@MainActor
class ProductDetailViewModel: ObservableObject {
    @Published var state: ProductDetailState
    @Published var effectTick: Int = 0
    private(set) var pendingEffect: ProductDetailEffect?

    private let viewStore: ProductDetailViewStore
    private let scope: Kotlinx_coroutines_coreCoroutineScope
    private var stateTask: Task<Void, Never>?
    private var effectsTask: Task<Void, Never>?

    init() {
        self.scope = ScopeHelperKt.createMainScope()
        self.viewStore = KoinHelper.shared.iosHelper.getProductDetailViewStore(scope: scope)
        self.state = viewStore.state.value
        observeState()
        observeEffects()
    }

    private func observeState() {
        stateTask = Task { @MainActor in
            do {
                for try await newState in viewStore.state {
                    self.state = newState
                }
            } catch {
                print("Error observing state: \(error)")
            }
        }
    }

    private func observeEffects() {
        effectsTask = Task { @MainActor in
            do {
                for try await effect in viewStore.effects {
                    self.pendingEffect = effect
                    self.effectTick += 1
                }
            } catch {
                print("Error observing effects: \(error)")
            }
        }
    }

    func onEvent(_ event: ProductDetailEvent) {
        viewStore.onEvent(event: event)
    }

    func dispatchAction(actionId: String, context: JsonObject) {
        viewStore.onEvent(event: ProductDetailEvent.DispatchAction(
            actionId: actionId,
            context: context,
            idempotencyKey: nil
        ))
    }

    deinit {
        ScopeHelperKt.cancelScope(scope: scope)
        stateTask?.cancel()
        effectsTask?.cancel()
    }
}
