//
// Created by Dimitrios Katoudis on 3/11/25.
//

import Foundation
import SwiftUI
import ComposeApp

struct HomeView: View {
    @StateObject private var productListViewModel = ProductListViewModel()
    @Binding var navigationPath : NavigationPath
    let onScrollOffsetChange: (CGFloat) -> Void
    
    var body: some View {
        ZStack {
            if productListViewModel.state.isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
            } else if let error = productListViewModel.state.error {
                VStack {
                    Text(String(localized: "error"))
                        .font(.headline)
                    switch onEnum(of: error) {
                    case .network(let networkError):
                        Text("Network error: \(networkError.error)")
                    case .noProducts:
                        Text(String(localized: "no_products"))
                    }
                }
            } else {
                ProductListContent(
                    categories: productListViewModel.state.categories,
                    favoritedProductIds: Set(productListViewModel.state.favoritedProductIds),
                    onProductClick: { productId, position in
                        navigationPath.append(
                            ProductRoute(
                                productId,
                                surface: InteractionSurface.homeShelf,
                                position: position
                            )
                        )
                    },
                    onToggleFavorite: { productId in
                        productListViewModel.toggleFavorite(productId: productId)
                    },
                    onScrollOffsetChange: onScrollOffsetChange
                )
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .background(FakeShopColors.background)
    }
}
