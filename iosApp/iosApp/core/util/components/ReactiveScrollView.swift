//
//  ReactiveScrollView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 20/05/26.
//

import SwiftUI

/// Pure SwiftUI scroll view with reactive search-bar offset tracking.
/// Embeds a zero-height UIViewRepresentable that KVO-observes the underlying
/// UIScrollView — avoids UIHostingController safe-area propagation issues.
struct ReactiveScrollView<Content: View>: View {
    let onScroll: (CGFloat) -> Void
    let content: Content

    init(onScroll: @escaping (CGFloat) -> Void, @ViewBuilder content: () -> Content) {
        self.onScroll = onScroll
        self.content = content()
    }

    private var totalHeight: CGFloat {
        let searchBarHeight: CGFloat = 58
        guard
            let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
            let window = scene.windows.first
        else { return searchBarHeight }
        return searchBarHeight + window.safeAreaInsets.top
    }

    var body: some View {
        ScrollView {
            content
                .background(
                    ScrollOffsetTracker(
                        totalHeight: totalHeight,
                        onSearchBarOffset: onScroll
                    )
                    .frame(height: 0)
                )
        }
    }
}

// MARK: - UIViewRepresentable bridge

private struct ScrollOffsetTracker: UIViewRepresentable {
    let totalHeight: CGFloat
    let onSearchBarOffset: (CGFloat) -> Void

    func makeUIView(context: Context) -> ScrollTrackerView {
        ScrollTrackerView()
    }

    func updateUIView(_ uiView: ScrollTrackerView, context: Context) {
        uiView.totalHeight = totalHeight
        uiView.onSearchBarOffset = onSearchBarOffset
    }
}

// MARK: - UIView that KVO-observes the enclosing UIScrollView

private class ScrollTrackerView: UIView {
    var totalHeight: CGFloat = 0
    var onSearchBarOffset: ((CGFloat) -> Void)?

    private var contentOffsetObs: NSKeyValueObservation?
    private var isDraggingObs: NSKeyValueObservation?
    private var isDeceleratingObs: NSKeyValueObservation?

    // Mirror of ScrollableVStack's internal state
    private var lastContentOffsetY: CGFloat = 0
    private var searchBarOffset: CGFloat = 0

    override func didMoveToWindow() {
        super.didMoveToWindow()
        invalidateObservations()
        guard window != nil else { return }

        var candidate: UIView? = superview
        while let view = candidate {
            if let sv = view as? UIScrollView {
                attach(to: sv)
                return
            }
            candidate = view.superview
        }
    }

    private func attach(to sv: UIScrollView) {
        // Delta tracking — only fires while user is actively dragging
        contentOffsetObs = sv.observe(\.contentOffset, options: .new) { [weak self, weak sv] _, change in
            guard let self, let sv, sv.isDragging, let y = change.newValue?.y else { return }
            guard y >= 0 else { return }
            let maxOffsetY = max(0, sv.contentSize.height - sv.bounds.height)
            guard y <= maxOffsetY else { return }

            let delta = y - self.lastContentOffsetY
            self.searchBarOffset = max(-self.totalHeight, min(0, self.searchBarOffset - delta))
            self.onSearchBarOffset?(self.searchBarOffset)
            self.lastContentOffsetY = y
        }

        // Capture start offset; snap if drag ends with no deceleration
        isDraggingObs = sv.observe(\.isDragging, options: .new) { [weak self, weak sv] _, change in
            guard let self, let sv else { return }
            if change.newValue == true {
                self.lastContentOffsetY = sv.contentOffset.y
            } else if !sv.isDecelerating {
                self.snap()
            }
        }

        // Snap when momentum scrolling ends
        isDeceleratingObs = sv.observe(\.isDecelerating, options: .new) { [weak self, weak sv] _, change in
            guard let self, let sv else { return }
            if change.newValue == false, !sv.isDragging {
                self.snap()
            }
        }
    }

    private func snap() {
        let threshold = totalHeight / 2
        searchBarOffset = searchBarOffset < -threshold ? -totalHeight : 0
        onSearchBarOffset?(searchBarOffset)
    }

    private func invalidateObservations() {
        contentOffsetObs?.invalidate()
        isDraggingObs?.invalidate()
        isDeceleratingObs?.invalidate()
        contentOffsetObs = nil
        isDraggingObs = nil
        isDeceleratingObs = nil
    }

    deinit { invalidateObservations() }
}
