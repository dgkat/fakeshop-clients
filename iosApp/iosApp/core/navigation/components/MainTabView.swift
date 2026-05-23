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
    @State private var scrollResetToken = UUID()

    private var isOnDetailScreen: Bool {
        homePath.count > 0 || favoritesPath.count > 0 ||
        notificationsPath.count > 0 || profilePath.count > 0
    }

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
                case .home: homePath.append(result.productId)
                case .favorites: favoritesPath.append(result.productId)
                case .notifications: notificationsPath.append(result.productId)
                case .profile: profilePath.append(result.productId)
                }
            }
        ) {
            TabView(selection: $selectedTab) {
                homeTab
                favoritesTab
                notificationsTab
                profileTab
            }
            .tint(FakeShopColors.primary)
            .toolbar(.hidden, for: .tabBar)
            .animation(.none, value: selectedTab)
        }
        .safeAreaInset(edge: .bottom, spacing: 0) {
            CustomTabBar(selectedTab: $selectedTab, isOnDetailScreen: isOnDetailScreen) { tab in
                switch tab {
                case .home: homePath = NavigationPath()
                case .favorites: favoritesPath = NavigationPath()
                case .notifications: notificationsPath = NavigationPath()
                case .profile: profilePath = NavigationPath()
                }
            }
        }
        .environment(\.scrollResetToken, scrollResetToken)
        .onChange(of: selectedTab) { oldValue, newValue in
            scrollOffset = 0
            scrollResetToken = UUID()
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                if newValue != .home { homePath = NavigationPath() }
                if newValue != .favorites { favoritesPath = NavigationPath() }
                if newValue != .notifications { notificationsPath = NavigationPath() }
                if newValue != .profile { profilePath = NavigationPath() }
            }
        }
        .onChange(of: homePath) { old, new in handleNavigationChange(oldPath: old, newPath: new, tab: .home) }
        .onChange(of: favoritesPath) { old, new in handleNavigationChange(oldPath: old, newPath: new, tab: .favorites) }
        .onChange(of: notificationsPath) { old, new in handleNavigationChange(oldPath: old, newPath: new, tab: .notifications) }
        .onChange(of: profilePath) { old, new in handleNavigationChange(oldPath: old, newPath: new, tab: .profile) }
        .onChange(of: notificationRouter.productIdToNavigate) { _, productId in
            if let productId {
                selectedTab = .home
                homePath.append(productId)
                notificationRouter.clearNavigation()
            }
        }
    }

    @ViewBuilder private var homeTab: some View {
        NavigationStack(path: $homePath) {
            HomeView(
                navigationPath: $homePath,
                onScrollOffsetChange: { offset in scrollOffset = offset }
            )
            .navigationDestination(for: String.self) { productId in
                ProductDetailView(
                    productId: productId,
                    onScrollOffsetChange: { offset in scrollOffset = offset }
                )
            }
        }
        .tabItem { Label(Tab.home.title, systemImage: selectedTab == .home ? Tab.home.iconFilled : Tab.home.icon) }
        .tag(Tab.home)
    }

    @ViewBuilder private var favoritesTab: some View {
        NavigationStack(path: $favoritesPath) {
            FavoritesView(
                navigationPath: $favoritesPath,
                onScrollOffsetChange: { offset in scrollOffset = offset }
            )
            .navigationDestination(for: String.self) { productId in
                ProductDetailView(
                    productId: productId,
                    onScrollOffsetChange: { offset in scrollOffset = offset }
                )
            }
        }
        .tabItem { Label(Tab.favorites.title, systemImage: selectedTab == .favorites ? Tab.favorites.iconFilled : Tab.favorites.icon) }
        .tag(Tab.favorites)
    }

    @ViewBuilder private var notificationsTab: some View {
        NavigationStack(path: $notificationsPath) {
            NotificationsView()
                .navigationDestination(for: String.self) { productId in
                    ProductDetailView(
                        productId: productId,
                        onScrollOffsetChange: { offset in scrollOffset = offset }
                    )
                    }
        }
        .tabItem { Label(Tab.notifications.title, systemImage: selectedTab == .notifications ? Tab.notifications.iconFilled : Tab.notifications.icon) }
        .tag(Tab.notifications)
    }

    @ViewBuilder private var profileTab: some View {
        NavigationStack(path: $profilePath) {
            ComposeProfileView()
                .navigationBarTitleDisplayMode(.inline)
                .navigationDestination(for: String.self) { productId in
                    ProductDetailView(
                        productId: productId,
                        onScrollOffsetChange: { offset in scrollOffset = offset }
                    )
                    }
        }
        .tabItem { Label(Tab.profile.title, systemImage: selectedTab == .profile ? Tab.profile.iconFilled : Tab.profile.icon) }
        .tag(Tab.profile)
    }

    private func handleNavigationChange(oldPath: NavigationPath, newPath: NavigationPath, tab: Tab) {
        let hasVisibleSearchBar = tab.searchBarBehavior != .hidden
        if hasVisibleSearchBar && newPath.count != oldPath.count {
            scrollOffset = 0
            scrollResetToken = UUID()
        }
    }
}

// MARK: - Custom tab bar

private struct CustomTabBar: View {
    @Binding var selectedTab: Tab
    let isOnDetailScreen: Bool
    let onTabReselected: (Tab) -> Void

    var body: some View {
        HStack(spacing: 0) {
            ForEach(Tab.allCases, id: \.self) { tab in
                let isSelected = !isOnDetailScreen && selectedTab == tab
                Button {
                    if selectedTab == tab {
                        onTabReselected(tab)
                    } else {
                        selectedTab = tab
                    }
                } label: {
                    VStack(spacing: 4) {
                        Image(systemName: isSelected ? tab.iconFilled : tab.icon)
                            .font(.system(size: 22))
                        Text(tab.title)
                            .font(.caption2)
                    }
                    .foregroundStyle(isSelected ? FakeShopColors.primary : Color(UIColor.systemGray))
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 8)
                }
                .buttonStyle(.plain)
            }
        }
        .background(FakeShopColors.surface)
        .overlay(alignment: .top) {
            Rectangle()
                .fill(Color(UIColor.separator))
                .frame(height: 0.5)
        }
    }
}

