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
    let onProductClick: (String) -> Void
    let onScrollOffsetChange: (CGFloat) -> Void
    
    var body: some View {
        ScrollableVStack(onScroll: onScrollOffsetChange) {
            LazyVStack(alignment: .leading, spacing: 16, pinnedViews: []) {
                ForEach(categories, id: \.category) { categoryRow in
                    CategorySection(
                        categoryRow: categoryRow,
                        onProductClick: onProductClick
                    )
                }
            }
            .padding(.vertical, 8)
        }
    }
}
