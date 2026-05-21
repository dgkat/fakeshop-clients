//
//  SearchBarContainer.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 18/12/25.
//

import SwiftUI
import ComposeApp

struct SearchBarContainer<Content: View>: View {
    @ObservedObject var searchViewModel: SearchViewModel
    let currentTab: Tab
    let behavior: SearchBarBehavior
    let onResultClick: (SearchResult) -> Void
    let content: Content

    @Binding var scrollOffset: CGFloat
    @State private var searchBarHeight: CGFloat = 72
    @State private var statusBarHeight: CGFloat = 0

    init(
        searchViewModel: SearchViewModel,
        currentTab: Tab,
        scrollOffset: Binding<CGFloat>,
        onResultClick: @escaping (SearchResult) -> Void,
        @ViewBuilder content: () -> Content
    ) {
        self.searchViewModel = searchViewModel
        self.currentTab = currentTab
        self.behavior = currentTab.searchBarBehavior
        self._scrollOffset = scrollOffset
        self.onResultClick = onResultClick
        self.content = content()
    }
    
    var body: some View {
        let state = searchViewModel.state
        
        GeometryReader { geometry in
            ZStack(alignment: .top) {
                content
                    .padding(.top, contentTopPadding)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .onAppear {
                        statusBarHeight = geometry.safeAreaInsets.top
                    }

                if state.isActive && behavior != .hidden {
                    Color.black.opacity(0.5)
                        .ignoresSafeArea()
                        .onTapGesture {
                            searchViewModel.onEvent(SearchEvent.CancelClicked())
                        }
                        .transition(.opacity)
                }

                VStack(spacing: 0) {
                    SearchBarView(
                        query: state.query,
                        onQueryChange: { query in
                            searchViewModel.onEvent(SearchEvent.QueryChanged(query: query))
                        },
                        onClear: {
                            searchViewModel.onEvent(SearchEvent.ClearQuery())
                        }
                    )
                    .background(
                        GeometryReader { geo in
                            Color.clear
                                .onAppear {
                                    searchBarHeight = geo.size.height
                                }
                        }
                    )
                    .background(FakeShopColors.surfaceContainer)
                    .shadow(
                        color: showShadow ? .black.opacity(0.1) : .clear,
                        radius: 4,
                        y: 2
                    )

                    // Search results dropdown
                    if state.isActive && behavior != .hidden {
                        SearchResultsView(
                            results: state.results,
                            isLoading: state.isLoading,
                            onResultClick: { result in
                                onResultClick(result)
                                searchViewModel.onEvent(SearchEvent.CancelClicked())
                            }
                        )
                        .transition(.opacity.combined(with: .move(edge: .top)))
                    }
                    
                    Spacer()
                }
                .frame(maxWidth: .infinity, alignment: .top)
                .offset(y: calculateOffset())
                .animation(.easeInOut(duration: 0.3), value: scrollOffset)
                .animation(.easeInOut(duration: 0.3), value: behavior)
            }
            .ignoresSafeArea(edges: .top)
        }
        .onChange(of: currentTab) { _, _ in
            scrollOffset = 0
        }
        .onChange(of: behavior) { _, newBehavior in
            if newBehavior != .hidden {
                scrollOffset = 0
            }
        }
    }

    private var contentTopPadding: CGFloat {
        guard behavior != .hidden else { return 0 }

        let totalHeight = searchBarHeight + statusBarHeight
        let visiblePortion = totalHeight + scrollOffset
        return max(0, visiblePortion)
    }

    private func calculateOffset() -> CGFloat {
        let offset: CGFloat

        switch behavior {
        case .hidden:
            offset = -searchBarHeight
        case .scrollReactive:
            offset = statusBarHeight + scrollOffset
        case .static:
            offset = statusBarHeight
        default:
            offset = statusBarHeight
        }

        return offset
    }

    private var showShadow: Bool {
        scrollOffset < -5 || behavior == .static
    }
}
