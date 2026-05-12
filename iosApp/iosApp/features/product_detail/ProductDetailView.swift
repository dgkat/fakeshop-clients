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

    @StateObject private var viewModel = ProductDetailViewModel()
    @StateObject private var actionToast = BduiActionToastState()

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
                    onRetry: {
                        viewModel.onEvent(ProductDetailEvent.Retry())
                    }
                )

            case .success(let successState):
                ProductContentView(
                    briefProduct: successState.product,
                    detailedState: viewModel.state.detailedState,
                    bduiBodyState: viewModel.state.bduiBodyState,
                    isFavorited: viewModel.state.isFavorited,
                    isFavoriteLoading: viewModel.state.isFavoriteLoading,
                    onToggleFavorite: { viewModel.onEvent(ProductDetailEvent.ToggleFavorite()) },
                    onAction: actionToast.makeHandler(),
                    onScrollOffsetChange: onScrollOffsetChange
                )
            }

            BduiActionToastOverlay(state: actionToast)
        }
        .navigationBarHidden(true)
        .onAppear {
            viewModel.onEvent(ProductDetailEvent.LoadProduct(productId: productId))
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
    let detailedState: DetailedProductState
    let bduiBodyState: BduiBodyState
    let isFavorited: Bool
    let isFavoriteLoading: Bool
    let onToggleFavorite: () -> Void
    let onAction: BduiActionHandler
    let onScrollOffsetChange: (CGFloat) -> Void

    var body: some View {
        ScrollableVStack(onScroll: onScrollOffsetChange) {
            LazyVStack(alignment: .leading, spacing: 0, pinnedViews: []) {
                // Image Section (top half — native, fed by brief + legacy detailedState gallery)
                ImageSection(
                    briefProduct: briefProduct,
                    detailedState: detailedState,
                    isFavorited: isFavorited,
                    isFavoriteLoading: isFavoriteLoading,
                    onToggleFavorite: onToggleFavorite
                )

                // Product Info Section
                VStack(alignment: .leading, spacing: 16) {
                    BriefProductInfo(product: briefProduct)

                    Divider()
                        .padding(.vertical, 8)

                    // BDUI body — server-driven bottom half
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
    let detailedState: DetailedProductState
    let isFavorited: Bool
    let isFavoriteLoading: Bool
    let onToggleFavorite: () -> Void

    var body: some View {
        ZStack(alignment: .topTrailing) {
            switch onEnum(of: detailedState) {
            case .success(let successState):
                if let galleryUrls = successState.product.galleryUrls, !galleryUrls.isEmpty {
                    ImageGallery(imageUrls: galleryUrls)
                } else {
                    SingleProductImage(imageUrl: briefProduct.imageUrl)
                }

            default:
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

// MARK: - Detailed Product Section
struct DetailedProductSection: View {
    let detailedState: DetailedProductState

    var body: some View {
        switch onEnum(of: detailedState) {
        case .loading:
            HStack(spacing: 8) {
                ProgressView()
                    .scaleEffect(0.8)
                Text(String(localized: "loading_details"))
                    .font(.body)
                    .foregroundColor(FakeShopColors.onSurfaceVariant)
            }

        case .success(let successState):
            DetailedProductInfo(product: successState.product)

        case .error:
            HStack(spacing: 8) {
                Image(systemName: "exclamationmark.triangle")
                    .foregroundColor(FakeShopColors.error)
                Text(String(localized: "error_product_details"))
                    .font(.body)
                    .foregroundColor(FakeShopColors.onErrorContainer)
            }
            .padding(.vertical, 8)
            .padding(.horizontal, 12)
            .background(FakeShopColors.errorContainer)
            .cornerRadius(8)
        }
    }
}

// MARK: - Detailed Product Info
struct DetailedProductInfo: View {
    let product: UiDetailedProduct

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            if let description = product.description_ as String?, !description.isEmpty {
                ProductSection(title: String(localized: "description"), content: description)
            }

            if let specs = product.specs as String?, !specs.isEmpty {
                ProductSection(title: String(localized: "specifications"), content: specs)
            }
        }
    }
}

// MARK: - Product Section
struct ProductSection: View {
    let title: String
    let content: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(.headline)
                .fontWeight(.semibold)

            Text(content)
                .font(.body)
                .foregroundColor(FakeShopColors.onSurfaceVariant)
                .fixedSize(horizontal: false, vertical: true)
        }
    }
}
