//
//  MainTabView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//
import SwiftUI
import ComposeApp

struct MainTabView: View {
    @StateObject private var searchViewModel = SearchViewModel()
    @StateObject private var notificationRouter = NotificationRouter.shared
    @StateObject private var navigationRouter = NavigationRouter.shared

    @State private var selectedTab: Tab = .home
    @State private var homePath = NavigationPath()
    @State private var favoritesPath = NavigationPath()
    @State private var notificationsPath = NavigationPath()
    @State private var profilePath = NavigationPath()
    @State private var offsetModel = SearchBarOffsetModel()
    @State private var scrollResetToken = UUID()

    private var isOnDetailScreen: Bool {
        homePath.count > 0 || favoritesPath.count > 0 ||
        notificationsPath.count > 0 || profilePath.count > 0
    }

    var body: some View {
        mainContent
            .environment(\.scrollResetToken, scrollResetToken)
            .onChange(of: selectedTab) { old, new in handleTabSwitch(oldValue: old, newValue: new) }
            .onChange(of: homePath) { old, new in handleNavigationChange(oldPath: old, newPath: new, tab: .home) }
            .onChange(of: favoritesPath) { old, new in handleNavigationChange(oldPath: old, newPath: new, tab: .favorites) }
            .onChange(of: notificationsPath) { old, new in handleNavigationChange(oldPath: old, newPath: new, tab: .notifications) }
            .onChange(of: profilePath) { old, new in handleNavigationChange(oldPath: old, newPath: new, tab: .profile) }
            .onChange(of: notificationRouter.productIdToNavigate) { _, productId in handleNotificationNavigate(productId) }
            .onChange(of: navigationRouter.requestToken) { _, _ in consumePendingRoute() }
            // Cold-start Universal Link: the request may land before this view mounts.
            .onAppear { consumePendingRoute() }
    }

    private var mainContent: some View {
        SearchBarContainer(
            searchViewModel: searchViewModel,
            currentTab: selectedTab,
            offsetModel: offsetModel,
            onResultClick: { result, position in
                let route = ProductRoute(
                    result.productId,
                    surface: InteractionSurface.search,
                    position: position
                )
                switch selectedTab {
                case .home: homePath.append(route)
                case .favorites: favoritesPath.append(route)
                case .notifications: notificationsPath.append(route)
                case .profile: profilePath.append(route)
                }
            }
        ) {
            ZStack {
                homeTab
                    .opacity(selectedTab == .home ? 1 : 0)
                    .allowsHitTesting(selectedTab == .home)
                favoritesTab
                    .opacity(selectedTab == .favorites ? 1 : 0)
                    .allowsHitTesting(selectedTab == .favorites)
                notificationsTab
                    .opacity(selectedTab == .notifications ? 1 : 0)
                    .allowsHitTesting(selectedTab == .notifications)
                profileTab
                    .opacity(selectedTab == .profile ? 1 : 0)
                    .allowsHitTesting(selectedTab == .profile)
            }
            .tint(FakeShopColors.primary)
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
    }

    private func handleTabSwitch(oldValue: Tab, newValue: Tab) {
        withAnimation(.easeInOut(duration: 0.3)) { offsetModel.offset = 0 }
        scrollResetToken = UUID()
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            if newValue != .home { homePath = NavigationPath() }
            if newValue != .favorites { favoritesPath = NavigationPath() }
            if newValue != .notifications { notificationsPath = NavigationPath() }
            if newValue != .profile { profilePath = NavigationPath() }
        }
    }

    private func handleNotificationNavigate(_ productId: String?) {
        guard let productId else { return }
        selectedTab = .home
        homePath.append(ProductRoute(productId))
        notificationRouter.clearNavigation()
    }

    private func consumePendingRoute() {
        guard let route = navigationRouter.pendingRoute else { return }
        let replace = navigationRouter.pendingReplace
        navigationRouter.clear()
        switch onEnum(of: route) {
        case .home:
            homePath = NavigationPath()
            selectedTab = .home
        case .favorites:
            favoritesPath = NavigationPath()
            selectedTab = .favorites
        case .notifications:
            notificationsPath = NavigationPath()
            selectedTab = .notifications
        case .profile:
            profilePath = NavigationPath()
            selectedTab = .profile
        case .productDetail(let r):
            pushProduct(r.productId, replace: replace)
        }
    }

    /// Pushes a PDP onto the active tab's stack.
    ///
    /// The `replace` flag (BDUI navigate's back-stack-replace) is intentionally **ignored on
    /// iOS for now** — a `replace` is treated as a plain push. Honouring it requires popping
    /// the current top and pushing on the next runloop tick (a single pop+append mutation is
    /// dropped by NavigationStack), which produces a visible pop→push double transition and an
    /// extra back-stack entry. Tracked as a deferred improvement in `code-review-findings.md`
    /// (item 31). The pushed entry is a `ProductRoute` (not a bare `String`) so navigating to a
    /// product already in the stack forms a distinct entry instead of a duplicate hashable
    /// value that NavigationStack no-ops on.
    private func pushProduct(_ productId: String, replace: Bool) {
        let route = ProductRoute(productId)
        switch selectedTab {
        case .home: homePath.append(route)
        case .favorites: favoritesPath.append(route)
        case .notifications: notificationsPath.append(route)
        case .profile: profilePath.append(route)
        }
    }

    @ViewBuilder private var homeTab: some View {
        NavigationStack(path: $homePath) {
            HomeView(
                navigationPath: $homePath,
                onScrollOffsetChange: { offset in offsetModel.offset = offset }
            )
            .navigationDestination(for: ProductRoute.self) { route in
                ProductDetailView(
                    productId: route.productId,
                    surface: route.surface,
                    position: route.position,
                    onScrollOffsetChange: { offset in offsetModel.offset = offset },
                    onNavigate: { url, replace in navigationRouter.navigate(url: url, replace: replace) }
                )
            }
        }
    }

    @ViewBuilder private var favoritesTab: some View {
        NavigationStack(path: $favoritesPath) {
            FavoritesView(
                navigationPath: $favoritesPath,
                isActive: selectedTab == .favorites,
                onScrollOffsetChange: { offset in offsetModel.offset = offset }
            )
            .navigationDestination(for: ProductRoute.self) { route in
                ProductDetailView(
                    productId: route.productId,
                    surface: route.surface,
                    position: route.position,
                    onScrollOffsetChange: { offset in offsetModel.offset = offset },
                    onNavigate: { url, replace in navigationRouter.navigate(url: url, replace: replace) }
                )
            }
        }
    }

    @ViewBuilder private var notificationsTab: some View {
        NavigationStack(path: $notificationsPath) {
            NotificationsView()
                .navigationDestination(for: ProductRoute.self) { route in
                    ProductDetailView(
                        productId: route.productId,
                        surface: route.surface,
                        position: route.position,
                        onScrollOffsetChange: { offset in offsetModel.offset = offset },
                        onNavigate: { url, replace in navigationRouter.navigate(url: url, replace: replace) }
                    )
                }
        }
    }

    @ViewBuilder private var profileTab: some View {
        NavigationStack(path: $profilePath) {
            ComposeProfileView()
                .toolbar(.hidden, for: .navigationBar)
                .navigationDestination(for: ProductRoute.self) { route in
                    ProductDetailView(
                        productId: route.productId,
                        surface: route.surface,
                        position: route.position,
                        onScrollOffsetChange: { offset in offsetModel.offset = offset },
                        onNavigate: { url, replace in navigationRouter.navigate(url: url, replace: replace) }
                    )
                }
        }
    }

    private func handleNavigationChange(oldPath: NavigationPath, newPath: NavigationPath, tab: Tab) {
        let hasVisibleSearchBar = tab.searchBarBehavior != .hidden
        if hasVisibleSearchBar && newPath.count != oldPath.count {
            withAnimation(.easeInOut(duration: 0.3)) { offsetModel.offset = 0 }
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
