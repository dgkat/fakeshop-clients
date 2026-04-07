//
//  ProductListContent.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 3/11/25.
//

import Foundation
import SwiftUI
import ComposeApp

struct ProductListContent: View {
    let categories: [UiCategoryRow]
    let favoritedProductIds: Set<String>
    let onProductClick: (String) -> Void
    let onToggleFavorite: (String) -> Void
    let onScrollOffsetChange: (CGFloat) -> Void

    var body: some View {
        ScrollableVStack(onScroll: onScrollOffsetChange) {
            LazyVStack(alignment: .leading, spacing: 16, pinnedViews: []) {
                ForEach(categories, id: \.category) { categoryRow in
                    CategorySection(
                        categoryRow: categoryRow,
                        favoritedProductIds: favoritedProductIds,
                        onProductClick: onProductClick,
                        onToggleFavorite: onToggleFavorite
                    )
                }
            }
            .padding(.vertical, 8)
        }
    }
}
