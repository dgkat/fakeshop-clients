//
//  SearchBarPlaceholder.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 3/11/25.
//

import Foundation
import SwiftUI
import ComposeApp

struct SearchBarPlaceholder: View {
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(FakeShopColors.onSurfaceVariant)
                Text(String(localized: "search_placeholder"))
                    .foregroundColor(FakeShopColors.onSurfaceVariant)
                Spacer()
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .frame(maxWidth: .infinity)
            .background(FakeShopColors.surfaceVariant)
            .cornerRadius(10)
        }
        .frame(height: 40)
        .padding(.horizontal)
    }
}
