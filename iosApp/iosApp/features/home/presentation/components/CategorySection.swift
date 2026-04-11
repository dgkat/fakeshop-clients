//
//  CategorySection.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 3/11/25.
//

import Foundation
import SwiftUI
import ComposeApp

struct CategorySection: View {
    let categoryRow: UiCategoryRow
    let favoritedProductIds: Set<String>
    let onProductClick: (String) -> Void
    let onToggleFavorite: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(categoryRow.category)
                .font(.title2)
                .fontWeight(.bold)
                .padding(.horizontal, 16)

            ProductRow(
                products: categoryRow.products,
                favoritedProductIds: favoritedProductIds,
                onProductClick: onProductClick,
                onToggleFavorite: onToggleFavorite
            )
        }
    }
}

// ProductRow.swift
struct ProductRow: View {
    let products: [UiBriefProduct]
    let favoritedProductIds: Set<String>
    let onProductClick: (String) -> Void
    let onToggleFavorite: (String) -> Void

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            LazyHStack(spacing: 12) {
                ForEach(products, id: \.id) { product in
                    ProductCard(
                        product: product,
                        isFavorited: favoritedProductIds.contains(product.id),
                        onClick: { onProductClick(product.id) },
                        onToggleFavorite: { onToggleFavorite(product.id) }
                    )
                }
            }
            .padding(.horizontal, 16)
        }
        .scrollClipDisabled()
        .scrollTargetBehavior(.viewAligned)
    }
}

// ProductCard.swift
struct ProductCard: View {
    let product: UiBriefProduct
    let isFavorited: Bool
    let onClick: () -> Void
    let onToggleFavorite: () -> Void

    @State private var isPressed: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ZStack(alignment: .topTrailing) {
                // Product Image
                AsyncImage(url: URL(string: product.imageUrl)) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                } placeholder: {
                    Rectangle()
                        .fill(FakeShopColors.surfaceVariant)
                        .overlay(
                            ProgressView()
                        )
                }
                .frame(width: 160, height: 160)
                .clipped()

                // Favorite button
                Button(action: onToggleFavorite) {
                    Image(systemName: isFavorited ? "heart.fill" : "heart")
                        .font(.system(size: 20, weight: .medium))
                        .foregroundColor(isFavorited ? FakeShopColors.error : .white)
                        .shadow(color: .black.opacity(0.35), radius: 4, x: 0, y: 1)
                }
                .buttonStyle(PlainButtonStyle())
                .padding(8)
            }

            // Product Info
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
        }
        .frame(width: 160)
        .background(FakeShopColors.surface)
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
        .contentShape(Rectangle())
        .scaleEffect(isPressed ? 0.97 : 1.0)
        .animation(.easeInOut(duration: 0.1), value: isPressed)
        .onTapGesture {
            onClick()
        }
        .simultaneousGesture(
            DragGesture(minimumDistance: 0)
                .onChanged { _ in isPressed = true }
                .onEnded { _ in isPressed = false }
        )
    }
}
