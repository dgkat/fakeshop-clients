//
//  SearchBarOffsetModel.swift
//  iosApp
//
//  Holds the scroll-reactive search-bar offset in a dedicated ObservableObject so that only the
//  floating search bar re-renders as the user scrolls. MainTabView owns it via `@State` (which
//  keeps a stable reference without subscribing to `objectWillChange`), so writing `offset` does
//  NOT re-evaluate MainTabView.body — the four always-alive NavigationStack tabs are no longer
//  re-diffed at scroll rate. Only SearchBarContainer observes it. See code-review-findings.md item 23.
//

import SwiftUI

final class SearchBarOffsetModel: ObservableObject {
    @Published var offset: CGFloat = 0
}
