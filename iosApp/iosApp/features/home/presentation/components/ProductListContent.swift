//
//  ProductListContent.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 3/11/25.
//

import Foundation
import SwiftUI
import Shared

struct ProductListContent: View {
    let categories: [CategoryRow]
    let onProductClick: (String) -> Void
    
    var body: some View {
        ScrollView {
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
