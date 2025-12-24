//
//  SearchResultsView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 18/12/25.
//

import SwiftUI
import ComposeApp

struct SearchResultRowHeightKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = max(value, nextValue())
    }
}

struct SearchResultsView: View {
    let results: [SearchResult]
    let isLoading: Bool
    let onResultClick: (SearchResult) -> Void
    
    @State private var rowHeight: CGFloat = 56
    private let maxVisibleItems = 10
    
    var body: some View {
        VStack(spacing: 0) {
            if isLoading {
                ProgressView()
                    .padding(32)
            } else if results.isEmpty {
                Text("No results found")
                    .foregroundColor(.secondary)
                    .padding(32)
            } else {
                let shouldScroll = results.count > maxVisibleItems
                let maxHeight = rowHeight * CGFloat(min(results.count, maxVisibleItems))
                
                ScrollView {
                    LazyVStack(spacing: 0) {
                        ForEach(results, id: \.productId) { result in
                            SearchResultRow(result: result)
                                .background(
                                    GeometryReader { geo in
                                        Color.clear
                                            .preference(
                                                key: SearchResultRowHeightKey.self,
                                                value: geo.size.height
                                            )
                                    }
                                )
                                .onTapGesture {
                                    onResultClick(result)
                                }
                            
                            if result.productId != results.last?.productId {
                                Divider()
                            }
                        }
                    }
                }
                .frame(maxHeight: maxHeight)
                .disabled(!shouldScroll)
                .onPreferenceChange(SearchResultRowHeightKey.self) { height in
                    if height > 0 {
                        rowHeight = height
                    }
                }
            }
        }
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 8, y: 4)
        .padding(.horizontal, 16)
    }
}

struct SearchResultRow: View {
    let result: SearchResult
    
    var body: some View {
        HStack(spacing: 12) {
            VStack(alignment: .leading, spacing: 4) {
                Text(result.productName)
                    .font(.body)
            }
            
            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .contentShape(Rectangle())
    }
}
