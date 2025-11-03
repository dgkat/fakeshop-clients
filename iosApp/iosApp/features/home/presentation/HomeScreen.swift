//
// Created by Dimitrios Katoudis on 3/11/25.
//

import Foundation
import SwiftUI
import Shared

struct HomeView: View {
    @StateObject private var searchViewModel = SearchViewmodel()
    @StateObject private var productListViewModel = ProductListViewModel()

    var body: some View {
        NavigationView {
            ZStack {
                if productListViewModel.state.isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                } else if let error = productListViewModel.state.error {
                    VStack {
                        Text("Error")
                            .font(.headline)
                        Text(error)
                            .foregroundColor(.secondary)
                    }
                } else {
                    ProductListContent(
                        categories: productListViewModel.state.categories,
                        onProductClick: { productId in
                            // TODO: Navigate to product details
                            print("Product clicked: \(productId)")
                        }
                    )
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    SearchBarPlaceholder {
                        // TODO: Navigate to search screen
                        print("Search tapped")
                    }
                }
            }
        }
    }
}