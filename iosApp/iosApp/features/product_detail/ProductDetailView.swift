//
//  ProductDetailView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis
//
import SwiftUI
import ComposeApp

struct ProductDetailView: View {
    let productId: String
    let onScrollOffsetChange: (CGFloat) -> Void
    var onNavigate: (String, Bool) -> Void = { _, _ in }

    @StateObject private var viewModel = ProductDetailViewModel()
    @StateObject private var toastState = BduiToastState()

    var body: some View {
        ZStack {
            let briefState = viewModel.state.briefState

            switch onEnum(of: briefState) {
            case .loading:
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())

            case .error(let errorState):
                ErrorView(
                    error: errorState.error,
                    onRetry: { viewModel.onEvent(ProductDetailEvent.Retry()) }
                )

            case .success(let successState):
                ProductContentView(
                    briefProduct: successState.product,
                    galleryUrls: viewModel.state.galleryUrls,
                    bduiBodyState: viewModel.state.bduiBodyState,
                    isFavorited: viewModel.state.isFavorited,
                    isFavoriteLoading: viewModel.state.isFavoriteLoading,
                    onToggleFavorite: { viewModel.onEvent(ProductDetailEvent.ToggleFavorite()) },
                    onAction: { actionId, context in viewModel.dispatchAction(actionId: actionId, context: context) },
                    onScrollOffsetChange: onScrollOffsetChange
                )
            }

            BduiToastOverlay(state: toastState)
        }
        .toolbar(.hidden, for: .navigationBar)
        .onAppear {
            viewModel.onEvent(ProductDetailEvent.LoadProduct(productId: productId))
        }
        .onChange(of: viewModel.effectTick) { _ in
            guard let effect = viewModel.pendingEffect else { return }
            switch onEnum(of: effect) {
            case .showToast(let e):
                toastState.show(e.message)
            case .navigate(let e):
                onNavigate(e.url, e.replace)
            }
        }
    }
}

// MARK: - Error View
struct ErrorView: View {
    let error: ProductDetailError
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text(errorMessage)
                .font(.body)
                .multilineTextAlignment(.center)
                .foregroundColor(FakeShopColors.onSurfaceVariant)

            Button(action: onRetry) {
                Text(String(localized: "retry"))
                    .fontWeight(.medium)
                    .foregroundColor(FakeShopColors.onPrimary)
                    .frame(width: 120, height: 44)
                    .background(FakeShopColors.primary)
                    .cornerRadius(12)
            }
        }
        .padding()
    }

    private var errorMessage: String {
        switch onEnum(of: error) {
        case .network:
            return String(localized: "error_network")
        case .productNotFound:
            return String(localized: "error_product_not_found")
        }
    }
}

// MARK: - Product Content View
struct ProductContentView: View {
    let briefProduct: UiBriefProduct
    let galleryUrls: [String]
    let bduiBodyState: BduiBodyState
    let isFavorited: Bool
    let isFavoriteLoading: Bool
    let onToggleFavorite: () -> Void
    let onAction: BduiActionHandler
    let onScrollOffsetChange: (CGFloat) -> Void

    var body: some View {
        ReactiveScrollView(onScroll: onScrollOffsetChange) {
            VStack(alignment: .leading, spacing: 0) {
                ImageSection(
                    briefProduct: briefProduct,
                    galleryUrls: galleryUrls,
                    isFavorited: isFavorited,
                    isFavoriteLoading: isFavoriteLoading,
                    onToggleFavorite: onToggleFavorite
                )

                VStack(alignment: .leading, spacing: 16) {
                    BriefProductInfo(product: briefProduct)

                    Divider()
                        .padding(.vertical, 8)

                    BduiBodySection(state: bduiBodyState, onAction: onAction)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
    }
}

// MARK: - Image Section
struct ImageSection: View {
    let briefProduct: UiBriefProduct
    let galleryUrls: [String]
    let isFavorited: Bool
    let isFavoriteLoading: Bool
    let onToggleFavorite: () -> Void

    var body: some View {
        ZStack(alignment: .topTrailing) {
            if !galleryUrls.isEmpty {
                ImageGallery(imageUrls: galleryUrls)
            } else {
                SingleProductImage(imageUrl: briefProduct.imageUrl)
            }

            FavoriteButton(
                isFavorited: isFavorited,
                isLoading: isFavoriteLoading,
                onToggle: onToggleFavorite
            )
            .padding(12)
        }
    }
}

// MARK: - Favorite Button
private struct FavoriteButton: View {
    let isFavorited: Bool
    let isLoading: Bool
    let onToggle: () -> Void

    var body: some View {
        Button(action: onToggle) {
            Image(systemName: isFavorited ? "heart.fill" : "heart")
                .font(.system(size: 28, weight: .medium))
                .foregroundColor(isFavorited ? FakeShopColors.error : .white)
                .shadow(color: .black.opacity(0.35), radius: 5, x: 0, y: 1)
        }
        .disabled(isLoading)
        .buttonStyle(PlainButtonStyle())
    }
}

// MARK: - Single Product Image
struct SingleProductImage: View {
    let imageUrl: String

    var body: some View {
        let screenWidth = UIScreen.main.bounds.width

        AsyncImage(url: URL(string: imageUrl)) { image in
            image
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(width: screenWidth, height: screenWidth)
        } placeholder: {
            Rectangle()
                .fill(FakeShopColors.surfaceVariant)
                .overlay(ProgressView())
                .frame(width: screenWidth, height: screenWidth)
        }
        .frame(width: screenWidth, height: screenWidth)
        .clipped()
    }
}

// MARK: - Image Gallery
struct ImageGallery: View {
    let imageUrls: [String]
    @State private var currentPage = 0

    var body: some View {
        let screenWidth = UIScreen.main.bounds.width

        VStack(spacing: 8) {
            TabView(selection: $currentPage) {
                ForEach(Array(imageUrls.enumerated()), id: \.offset) { index, imageUrl in
                    AsyncImage(url: URL(string: imageUrl)) { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: screenWidth, height: screenWidth)
                    } placeholder: {
                        Rectangle()
                            .fill(FakeShopColors.surfaceVariant)
                            .overlay(ProgressView())
                            .frame(width: screenWidth, height: screenWidth)
                    }
                    .frame(width: screenWidth, height: screenWidth)
                    .clipped()
                    .tag(index)
                }
            }
            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            .frame(width: screenWidth, height: screenWidth)

            // Page Indicator
            if imageUrls.count > 1 {
                HStack(spacing: 8) {
                    ForEach(0..<imageUrls.count, id: \.self) { index in
                        Circle()
                            .fill(index == currentPage ? FakeShopColors.primary : FakeShopColors.outline.opacity(0.3))
                            .frame(width: 8, height: 8)
                    }
                }
                .padding(.bottom, 8)
            }
        }
    }
}

// MARK: - Brief Product Info
struct BriefProductInfo: View {
    let product: UiBriefProduct

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(product.category)
                .font(.subheadline)
                .fontWeight(.medium)
                .foregroundColor(FakeShopColors.primary)

            Text(product.name)
                .font(.title2)
                .fontWeight(.bold)

            Text(String(format: "$%.2f", product.price))
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(FakeShopColors.primary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.top, 16)
    }
}

