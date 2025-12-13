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
    let categoryRow: CategoryRow
    let onProductClick: (String) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(categoryRow.category)
                .font(.title2)
                .fontWeight(.bold)
                .padding(.horizontal, 16)
            
            ProductRow(
                products: categoryRow.products,
                onProductClick: onProductClick
            )
        }
    }
}

// ProductRow.swift
struct ProductRow: View {
    let products: [UiBriefProduct]
    let onProductClick: (String) -> Void
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            LazyHStack(spacing: 12) {
                ForEach(products, id: \.id) { product in
                    ProductCard(
                        product: product,
                        onClick: { onProductClick(product.id) }
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
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 0) {
                // Product Image
                AsyncImage(url: URL(string: product.imageUrl)) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                } placeholder: {
                    Rectangle()
                        .fill(Color(.systemGray5))
                        .overlay(
                            ProgressView()
                        )
                }
                .frame(width: 160, height: 160)
                .clipped()
                
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
                        .foregroundColor(.blue)
                }
                .padding(12)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .frame(width: 160)
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
        }
        .buttonStyle(CardButtonStyle())
    }
}

struct CardButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.97 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}
