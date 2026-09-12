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
    let onResultClick: (SearchResult, Int) -> Void
    let content: Content

    // Only this container observes the offset, so scrolling re-renders the search bar alone —
    // not MainTabView and its four always-alive tabs. See code-review-findings.md item 23.
    @ObservedObject var offsetModel: SearchBarOffsetModel
    @State private var searchBarHeight: CGFloat = 72
    @State private var statusBarHeight: CGFloat = 0

    init(
        searchViewModel: SearchViewModel,
        currentTab: Tab,
        offsetModel: SearchBarOffsetModel,
        onResultClick: @escaping (SearchResult, Int) -> Void,
        @ViewBuilder content: () -> Content
    ) {
        self.searchViewModel = searchViewModel
        self.currentTab = currentTab
        self.behavior = currentTab.searchBarBehavior
        self.offsetModel = offsetModel
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
                            onResultClick: { result, position in
                                onResultClick(result, position)
                                searchViewModel.onEvent(SearchEvent.CancelClicked())
                            }
                        )
                        .transition(.opacity.combined(with: .move(edge: .top)))
                    }
                    
                    Spacer()
                }
                .frame(maxWidth: .infinity, alignment: .top)
                .offset(y: calculateOffset())
                // No `.animation(value: scrollOffset)` — continuous drag follows the finger 1:1 and
                // the snap is animated at its source in ReactiveScrollView (item 23). Behavior
                // changes (route-level, rare) stay animated.
                .animation(.easeInOut(duration: 0.3), value: behavior)
            }
            .ignoresSafeArea(edges: .top)
        }
        .onChange(of: currentTab) { _, _ in
            withAnimation(.easeInOut(duration: 0.3)) { offsetModel.offset = 0 }
        }
        .onChange(of: behavior) { _, newBehavior in
            if newBehavior != .hidden {
                withAnimation(.easeInOut(duration: 0.3)) { offsetModel.offset = 0 }
            }
        }
    }

    private var contentTopPadding: CGFloat {
        switch behavior {
        case .hidden: return 0
        case .scrollReactive: return statusBarHeight
        default: return searchBarHeight + statusBarHeight
        }
    }

    private func calculateOffset() -> CGFloat {
        let offset: CGFloat

        switch behavior {
        case .hidden:
            offset = -searchBarHeight
        case .scrollReactive:
            offset = statusBarHeight + offsetModel.offset
        case .static:
            offset = statusBarHeight
        default:
            offset = statusBarHeight
        }

        return offset
    }

    private var showShadow: Bool {
        offsetModel.offset < -5 || behavior == .static
    }
}
