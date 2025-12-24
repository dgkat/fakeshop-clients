//
//  MainTabView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//
import SwiftUI

struct MainTabView: View {
    @StateObject private var searchViewModel = SearchViewModel()
    
    @State private var selectedTab: Tab = .home
    @State private var homePath = NavigationPath()
    @State private var favoritesPath = NavigationPath()
    @State private var notificationsPath = NavigationPath()
    @State private var profilePath = NavigationPath()
    
    @State private var scrollOffset: CGFloat = 0
    
    init() {
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor.systemBackground
        
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
                        ProductDetailView(productId: productId)
                    }
                }
                .tabItem {
                    Label(Tab.home.rawValue, systemImage: selectedTab == .home ? Tab.home.iconFilled : Tab.home.icon)
                }
                .tag(Tab.home)
                
                NavigationStack(path: $favoritesPath) {
                    FavoritesView(
//                        onScrollOffsetChange: { offset in
//                            scrollOffset = offset
//                        }
                    )
                    .navigationDestination(for: String.self) { productId in
                        ProductDetailView(productId: productId)
                    }
                }
                .tabItem {
                    Label(Tab.favorites.rawValue, systemImage: selectedTab == .favorites ? Tab.favorites.iconFilled : Tab.favorites.icon)
                }
                .tag(Tab.favorites)
                
                NavigationStack(path: $notificationsPath) {
                    NotificationsView()
                        .navigationDestination(for: String.self) { productId in
                            ProductDetailView(productId: productId)
                        }
                }
                .tabItem {
                    Label(Tab.notifications.rawValue, systemImage: selectedTab == .notifications ? Tab.notifications.iconFilled : Tab.notifications.icon)
                }
                .tag(Tab.notifications)
                
                NavigationStack(path: $profilePath) {
                    ComposeProfileView()
                        .navigationBarTitleDisplayMode(.inline)
                        .navigationTitle("Profile")
                        .navigationDestination(for: String.self) { productId in
                            ProductDetailView(productId: productId)
                        }
                }
                .tabItem {
                    Label(Tab.profile.rawValue, systemImage: selectedTab == .profile ? Tab.profile.iconFilled : Tab.profile.icon)
                }
                .tag(Tab.profile)
            }
            .tint(.blue)
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
    }
}
