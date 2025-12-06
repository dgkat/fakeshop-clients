//
//  ProductDetailView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//
import SwiftUI

struct ProductDetailView: View {
    let productId: String
    
    var body: some View {
        ZStack {
            VStack(alignment: .leading, spacing: 20) {
                Text(productId)
                    .font(.body)
                    .foregroundColor(.secondary)
            }
            .padding()
        }
        .navigationTitle("Product Details")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            //TODO load items
        }
    }
}
