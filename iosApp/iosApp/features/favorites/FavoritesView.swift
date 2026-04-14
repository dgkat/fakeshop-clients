//
//  FavoritesView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//
import SwiftUI
import ComposeApp

struct FavoritesView: View {
    @StateObject private var viewModel = FavoritesViewModel()
    @Binding var navigationPath: NavigationPath
    let onScrollOffsetChange: (CGFloat) -> Void

    var body: some View {
        Group {
            if let isLoggedIn = viewModel.isLoggedIn {
                if isLoggedIn {
                    FavoritesTabbedContent(
                        viewModel: viewModel,
                        navigationPath: $navigationPath,
                        onScrollOffsetChange: onScrollOffsetChange
                    )
                } else {
                    LoginRequiredContent(onGoToProfile: {})
                }
            } else {
                ProgressView()
            }
        }
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - Tabbed Content

private struct FavoritesTabbedContent: View {
    @ObservedObject var viewModel: FavoritesViewModel
    @Binding var navigationPath: NavigationPath
    let onScrollOffsetChange: (CGFloat) -> Void
    @State private var selectedTab = 0

    var body: some View {
        VStack(spacing: 0) {
            NotificationPermissionBanner(
                permissionStatus: viewModel.favoritesState.notificationPermissionStatus,
                showBanner: viewModel.favoritesState.showNotificationBanner,
                onPermissionResult: { granted in
                    viewModel.onFavoritesEvent(FavoritesEvent.NotificationPermissionResult(granted: granted))
                }
            )

            Picker("", selection: $selectedTab) {
                Text(String(localized: "tab_favorites")).tag(0)
                Text(String(localized: "tab_recently_seen")).tag(1)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)

            TabView(selection: $selectedTab) {
                FavoritesTabContent(
                    viewModel: viewModel,
                    navigationPath: $navigationPath,
                    onScrollOffsetChange: onScrollOffsetChange
                )
                .tag(0)

                RecentsTabContent(
                    viewModel: viewModel,
                    navigationPath: $navigationPath,
                    onScrollOffsetChange: onScrollOffsetChange
                )
                .tag(1)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .animation(.easeInOut(duration: 0.25), value: selectedTab)
        }
    }
}

// MARK: - Favorites Tab

private struct FavoritesTabContent: View {
    @ObservedObject var viewModel: FavoritesViewModel
    @Binding var navigationPath: NavigationPath
    let onScrollOffsetChange: (CGFloat) -> Void

    var body: some View {
        Group {
            let state = viewModel.favoritesState

            if state.error != nil {
                NetworkErrorContent(onRetry: {
                    viewModel.onFavoritesEvent(FavoritesEvent.Retry())
                })
            } else if state.isLoading {
                Spacer()
                ProgressView()
                Spacer()
            } else if state.products.isEmpty {
                FavoritesEmptyContent()
            } else {
                ProductGrid(
                    products: state.products,
                    onProductClick: { productId in
                        navigationPath.append(productId)
                    },
                    onRemoveFavorite: { productId in
                        viewModel.onFavoritesEvent(FavoritesEvent.RemoveFavorite(productId: productId))
                    },
                    onScrollOffsetChange: onScrollOffsetChange
                )
            }
        }
        .onAppear {
            viewModel.onFavoritesEvent(FavoritesEvent.LoadFavorites())
        }
    }
}

// MARK: - Recents Tab

private struct RecentsTabContent: View {
    @ObservedObject var viewModel: FavoritesViewModel
    @Binding var navigationPath: NavigationPath
    let onScrollOffsetChange: (CGFloat) -> Void

    var body: some View {
        Group {
            let state = viewModel.recentsState

            if state.error != nil {
                NetworkErrorContent(onRetry: {
                    viewModel.onRecentsEvent(RecentsEvent.Retry())
                })
            } else if state.isLoading {
                Spacer()
                ProgressView()
                Spacer()
            } else if state.products.isEmpty {
                RecentsEmptyContent()
            } else {
                ProductGrid(
                    products: state.products,
                    onProductClick: { productId in
                        navigationPath.append(productId)
                    },
                    onRemoveFavorite: nil,
                    onScrollOffsetChange: onScrollOffsetChange
                )
            }
        }
        .onAppear {
            viewModel.onRecentsEvent(RecentsEvent.LoadRecents())
        }
    }
}

// MARK: - Product Grid

private struct ProductGrid: View {
    let products: [UiBriefProduct]
    let onProductClick: (String) -> Void
    let onRemoveFavorite: ((String) -> Void)?
    let onScrollOffsetChange: (CGFloat) -> Void

    private let columns = [
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12)
    ]

    var body: some View {
        ScrollableVStack(onScroll: onScrollOffsetChange) {
            LazyVGrid(columns: columns, spacing: 12) {
                ForEach(products, id: \.id) { product in
                    ProductCardWithHeart(
                        product: product,
                        isFavorited: onRemoveFavorite != nil,
                        onClick: { onProductClick(product.id) },
                        onToggleFavorite: { onRemoveFavorite?(product.id) }
                    )
                }
            }
            .padding(16)
        }
    }
}

// MARK: - Product Card with Heart

struct ProductCardWithHeart: View {
    let product: UiBriefProduct
    let isFavorited: Bool
    let onClick: () -> Void
    let onToggleFavorite: () -> Void

    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 0) {
                ZStack(alignment: .topTrailing) {
                    Color.clear
                        .frame(height: 140)
                        .overlay(
                            AsyncImage(url: URL(string: product.imageUrl)) { image in
                                image
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                            } placeholder: {
                                Rectangle()
                                    .fill(FakeShopColors.surfaceVariant)
                                    .overlay(ProgressView())
                            }
                        )
                        .clipped()

                    Button(action: onToggleFavorite) {
                        Image(systemName: isFavorited ? "heart.fill" : "heart")
                            .font(.system(size: 20, weight: .medium))
                            .foregroundColor(isFavorited ? FakeShopColors.error : .white)
                            .shadow(color: .black.opacity(0.35), radius: 4, x: 0, y: 1)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .padding(8)
                }

                VStack(alignment: .leading, spacing: 4) {
                    Text(product.name)
                        .font(.body)
                        .fontWeight(.medium)
                        .foregroundColor(.primary)
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)

                    Text(String(format: "$%.2f", product.price))
                        .font(.title3)
                        .fontWeight(.bold)
                        .foregroundColor(FakeShopColors.primary)
                }
                .padding(12)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .background(FakeShopColors.surface)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
        }
        .buttonStyle(CardButtonStyle())
    }
}

// MARK: - Empty & Error States

private struct LoginRequiredContent: View {
    let onGoToProfile: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            Image(systemName: "heart.fill")
                .font(.system(size: 64))
                .foregroundColor(FakeShopColors.onSurfaceVariant)

            Text(String(localized: "login_required_message"))
                .font(.body)
                .foregroundColor(FakeShopColors.onSurfaceVariant)
                .multilineTextAlignment(.center)

            Button(action: onGoToProfile) {
                Text(String(localized: "go_to_profile"))
                    .fontWeight(.medium)
                    .foregroundColor(FakeShopColors.onPrimary)
                    .frame(width: 160, height: 44)
                    .background(FakeShopColors.primary)
                    .cornerRadius(12)
            }
            Spacer()
        }
        .padding(32)
    }
}

private struct FavoritesEmptyContent: View {
    var body: some View {
        VStack(spacing: 8) {
            Spacer()
            Image(systemName: "heart.fill")
                .font(.system(size: 64))
                .foregroundColor(FakeShopColors.onSurfaceVariant)

            Text(String(localized: "favorites_empty"))
                .font(.body)
                .foregroundColor(FakeShopColors.onSurfaceVariant)
                .multilineTextAlignment(.center)
            Spacer()
        }
        .padding(32)
    }
}

private struct RecentsEmptyContent: View {
    var body: some View {
        VStack(spacing: 8) {
            Spacer()
            Image(systemName: "clock")
                .font(.system(size: 64))
                .foregroundColor(FakeShopColors.onSurfaceVariant)

            Text(String(localized: "recents_empty"))
                .font(.body)
                .foregroundColor(FakeShopColors.onSurfaceVariant)
                .multilineTextAlignment(.center)
            Spacer()
        }
        .padding(32)
    }
}

private struct NetworkErrorContent: View {
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            Text(String(localized: "error_network"))
                .font(.body)

            Button(action: onRetry) {
                Text(String(localized: "retry"))
                    .fontWeight(.medium)
                    .foregroundColor(FakeShopColors.onPrimary)
                    .frame(width: 120, height: 44)
                    .background(FakeShopColors.primary)
                    .cornerRadius(12)
            }
            Spacer()
        }
    }
}
