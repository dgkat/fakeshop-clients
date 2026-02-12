//
//  SearchBarView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 18/12/25.
//

import SwiftUI

struct SearchBarView: View {
    let query: String
    let onQueryChange: (String) -> Void
    let onClear: () -> Void
    
    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: "magnifyingglass")
                .foregroundColor(FakeShopColors.onSurfaceVariant)
            
            TextField(String(localized: "search_placeholder"), text: Binding(
                get: { query },
                set: { onQueryChange($0) }
            ))
            .textFieldStyle(.plain)
            .autocorrectionDisabled()
            
            if !query.isEmpty {
                Button(action: onClear) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(FakeShopColors.onSurfaceVariant)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 10)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(FakeShopColors.surfaceVariant)
        )
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }
}
