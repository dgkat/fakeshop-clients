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
                    Text("Error")
                        .font(.headline)
                    Text(error)
                        .foregroundColor(.secondary)
                }
            } else {
                ProductListContent(
                    categories: productListViewModel.state.categories,
                    onProductClick: { productId in
                        navigationPath.append(productId)
                    },
                    onScrollOffsetChange: onScrollOffsetChange 
                )
            }
        }
        .navigationBarTitleDisplayMode(.inline)
    }
}
