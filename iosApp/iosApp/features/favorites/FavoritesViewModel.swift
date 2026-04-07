//
//  FavoritesViewModel.swift
//  iosApp
//

import SwiftUI
import ComposeApp

@MainActor
class FavoritesViewModel: ObservableObject {
    @Published var favoritesState: FavoritesState
    @Published var recentsState: RecentsState
    @Published var isLoggedIn: Bool? = nil

    private let favoritesStore: FavoritesViewStore
    private let recentsStore: RecentsViewStore
    private let profileService: ProfileService
    private let scope: Kotlinx_coroutines_coreCoroutineScope
    private var favoritesObservationTask: Task<Void, Never>?
    private var recentsObservationTask: Task<Void, Never>?

    init() {
        self.scope = ScopeHelperKt.createMainScope()
        let helper = KoinHelper.shared.iosHelper
        self.favoritesStore = helper.getFavoritesViewStore(scope: scope)
        self.recentsStore = helper.getRecentsViewStore(scope: scope)
        self.profileService = helper.getProfileService()
        self.favoritesState = favoritesStore.state.value
        self.recentsState = recentsStore.state.value
        observeFavoritesState()
        observeRecentsState()
        checkLoginAndLoad()
    }

    private func observeFavoritesState() {
        favoritesObservationTask = Task { @MainActor in
            do {
                for try await newState in favoritesStore.state {
                    self.favoritesState = newState
                }
            } catch {
                print("Error observing favorites state: \(error)")
            }
        }
    }

    private func observeRecentsState() {
        recentsObservationTask = Task { @MainActor in
            do {
                for try await newState in recentsStore.state {
                    self.recentsState = newState
                }
            } catch {
                print("Error observing recents state: \(error)")
            }
        }
    }

    private func checkLoginAndLoad() {
        Task {
            do {
                let result = try await profileService.checkLoginStatus()
                if let success = result as? ResultSuccess<KotlinBoolean>,
                   let loggedIn = success.data?.boolValue {
                    self.isLoggedIn = loggedIn
                    if loggedIn {
                        favoritesStore.onEvent(event: FavoritesEvent.LoadFavorites())
                    }
                } else {
                    self.isLoggedIn = false
                }
            } catch {
                self.isLoggedIn = false
            }
        }
    }

    func onFavoritesEvent(_ event: FavoritesEvent) {
        favoritesStore.onEvent(event: event)
    }

    func onRecentsEvent(_ event: RecentsEvent) {
        recentsStore.onEvent(event: event)
    }

    deinit {
        ScopeHelperKt.cancelScope(scope: scope)
        favoritesObservationTask?.cancel()
        recentsObservationTask?.cancel()
    }
}
