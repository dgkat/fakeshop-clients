//
//  MainTabView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//
import SwiftUI

struct MainTabView: View {
    @StateObject private var searchViewModel = SearchViewModel()
    @StateObject private var notificationRouter = NotificationRouter.shared

    @State private var selectedTab: Tab = .home
    @State private var homePath = NavigationPath()
    @State private var favoritesPath = NavigationPath()
    @State private var notificationsPath = NavigationPath()
    @State private var profilePath = NavigationPath()
    
    @State private var scrollOffset: CGFloat = 0
    
    init() {
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor(FakeShopColors.surface)
        
        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
    }
    
    var body: some View {
        SearchBarContainer(
            searchViewModel: searchViewModel,
            currentTab: selectedTab,
            scrollOffset: $scrollOffset,
            onResultClick: { result in
                switch selectedTab {
                case .home:
                    homePath.append(result.productId)
                case .favorites:
                    favoritesPath.append(result.productId)
                case .notifications:
                    notificationsPath.append(result.productId)
                case .profile:
                    profilePath.append(result.productId)
                }
            }
        ) {
            TabView(selection: $selectedTab) {
                
                NavigationStack(path: $homePath) {
                    HomeView(
                        navigationPath: $homePath,
                        onScrollOffsetChange: { offset in
                            scrollOffset = offset
                        }
                    )
                    .navigationDestination(for: String.self) { productId in
                        ProductDetailView(
                            productId: productId,
                            onScrollOffsetChange: { offset in
                                scrollOffset = offset
                            }
                        )
                    }
                }
                .tabItem {
                    Label(Tab.home.title, systemImage: selectedTab == .home ? Tab.home.iconFilled : Tab.home.icon)
                }
                .tag(Tab.home)
                
                NavigationStack(path: $favoritesPath) {
                    FavoritesView(
                        navigationPath: $favoritesPath,
                        onScrollOffsetChange: { offset in
                            scrollOffset = offset
                        }
                    )
                    .navigationDestination(for: String.self) { productId in
                        ProductDetailView(
                            productId: productId,
                            onScrollOffsetChange: { offset in
                                scrollOffset = offset
                            }
                        )
                    }
                }
                .tabItem {
                    Label(Tab.favorites.title, systemImage: selectedTab == .favorites ? Tab.favorites.iconFilled : Tab.favorites.icon)
                }
                .tag(Tab.favorites)
                
                NavigationStack(path: $notificationsPath) {
                    NotificationsView()
                        .navigationDestination(for: String.self) { productId in
                            ProductDetailView(
                                productId: productId,
                                onScrollOffsetChange: { offset in
                                    scrollOffset = offset
                                }
                            )
                        }
                }
                .tabItem {
                    Label(Tab.notifications.title, systemImage: selectedTab == .notifications ? Tab.notifications.iconFilled : Tab.notifications.icon)
                }
                .tag(Tab.notifications)
                
                NavigationStack(path: $profilePath) {
                    ComposeProfileView()
                        .navigationBarTitleDisplayMode(.inline)
                        .navigationTitle(Tab.profile.title)
                        .navigationDestination(for: String.self) { productId in
                            ProductDetailView(
                                productId: productId,
                                onScrollOffsetChange: { offset in
                                    scrollOffset = offset
                                }
                            )
                        }
                }
                .tabItem {
                    Label(Tab.profile.title, systemImage: selectedTab == .profile ? Tab.profile.iconFilled : Tab.profile.icon)
                }
                .tag(Tab.profile)
            }
            .tint(FakeShopColors.primary)
        }
        .onChange(of: selectedTab) { oldValue, newValue in
            // Reset scroll offset when changing tabs
            scrollOffset = 0
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                if newValue != .home {
                    homePath = NavigationPath()
                }
                if newValue != .favorites {
                    favoritesPath = NavigationPath()
                }
                if newValue != .notifications {
                    notificationsPath = NavigationPath()
                }
                if newValue != .profile {
                    profilePath = NavigationPath()
                }
            }
        }
        .onChange(of: homePath) { oldValue, newValue in
            handleNavigationChange(oldPath: oldValue, newPath: newValue, tab: .home)
        }
        .onChange(of: favoritesPath) { oldValue, newValue in
            handleNavigationChange(oldPath: oldValue, newPath: newValue, tab: .favorites)
        }
        .onChange(of: notificationsPath) { oldValue, newValue in
            handleNavigationChange(oldPath: oldValue, newPath: newValue, tab: .notifications)
        }
        .onChange(of: profilePath) { oldValue, newValue in
            handleNavigationChange(oldPath: oldValue, newPath: newValue, tab: .profile)
        }
        .onChange(of: notificationRouter.productIdToNavigate) { _, productId in
            if let productId {
                selectedTab = .home
                homePath.append(productId)
                notificationRouter.clearNavigation()
            }
        }
    }

    private func handleNavigationChange(oldPath: NavigationPath, newPath: NavigationPath, tab: Tab) {
        // Only reset scroll offset when navigating to a screen with a visible search bar
        let hasVisibleSearchBar = tab.searchBarBehavior != .hidden

        // Reset on any navigation (push or pop) if search bar should be visible
        if hasVisibleSearchBar && newPath.count != oldPath.count {
            scrollOffset = 0
        }
    }
}
