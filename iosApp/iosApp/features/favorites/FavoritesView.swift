//
//  FavoritesView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//
import SwiftUI

struct FavoritesView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "heart.fill")
                .font(.system(size: 64))
                .foregroundColor(FakeShopColors.primary)
            
            Text("Favorites")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text("Your favorite products will appear here")
                .font(.body)
                .foregroundColor(.secondary)
        }
        .navigationTitle("Favorites")
    }
}
