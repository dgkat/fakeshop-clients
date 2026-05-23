//
//  ReactiveScrollView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 20/05/26.
//

import SwiftUI

// MARK: - Environment key for external reset signal

private struct ScrollResetTokenKey: EnvironmentKey {
    static let defaultValue = UUID()
}

extension EnvironmentValues {
    var scrollResetToken: UUID {
        get { self[ScrollResetTokenKey.self] }
        set { self[ScrollResetTokenKey.self] = newValue }
    }
}

// MARK: - ReactiveScrollView

/// Pure SwiftUI scroll view with reactive search-bar offset tracking.
/// Embeds a zero-height UIViewRepresentable that KVO-observes the underlying
/// UIScrollView — avoids UIHostingController safe-area propagation issues.
struct ReactiveScrollView<Content: View>: View {
    let onScroll: (CGFloat) -> Void
    let showTopInset: Bool
    let content: Content

    @Environment(\.scrollResetToken) private var resetToken

    init(onScroll: @escaping (CGFloat) -> Void, showTopInset: Bool = true, @ViewBuilder content: () -> Content) {
        self.onScroll = onScroll
        self.showTopInset = showTopInset
        self.content = content()
    }

    private let searchBarHeight: CGFloat = 58

    private var totalHeight: CGFloat {
        guard
            let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
            let window = scene.windows.first
        else { return searchBarHeight }
        return searchBarHeight + window.safeAreaInsets.top
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                if showTopInset {
                    Color.clear.frame(height: searchBarHeight)
                }
                content
                    .background(
                        ScrollOffsetTracker(
                            totalHeight: totalHeight,
                            onSearchBarOffset: onScroll,
                            resetToken: resetToken
                        )
                        .frame(height: 0)
                    )
            }
        }
    }
}

// MARK: - UIViewRepresentable bridge

private struct ScrollOffsetTracker: UIViewRepresentable {
    let totalHeight: CGFloat
    let onSearchBarOffset: (CGFloat) -> Void
    let resetToken: UUID

    func makeUIView(context: Context) -> ScrollTrackerView {
        ScrollTrackerView()
    }

    func updateUIView(_ uiView: ScrollTrackerView, context: Context) {
        uiView.totalHeight = totalHeight
        uiView.onSearchBarOffset = onSearchBarOffset
        if uiView.lastResetToken != resetToken {
            uiView.lastResetToken = resetToken
            uiView.resetOffset()
        }
    }
}

// MARK: - UIView that attaches a delegate proxy to the enclosing UIScrollView

private class ScrollTrackerView: UIView {
    var totalHeight: CGFloat = 0
    var onSearchBarOffset: ((CGFloat) -> Void)?
    var lastResetToken: UUID = UUID()

    private var contentOffsetObs: NSKeyValueObservation?
    private weak var trackedScrollView: UIScrollView?
    private var delegateProxy: ScrollDelegateProxy?

    private var lastContentOffsetY: CGFloat = 0
    private var searchBarOffset: CGFloat = 0

    func resetOffset() {
        searchBarOffset = 0
        lastContentOffsetY = trackedScrollView?.contentOffset.y ?? 0
    }

    override func didMoveToWindow() {
        super.didMoveToWindow()
        detach()
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
        trackedScrollView = sv

        let proxy = ScrollDelegateProxy(original: sv.delegate)
        proxy.onWillBeginDragging = { [weak self, weak sv] in
            self?.lastContentOffsetY = sv?.contentOffset.y ?? 0
        }
        proxy.onDidEndDragging = { [weak self] willDecelerate in
            if !willDecelerate { self?.snap() }
        }
        proxy.onDidEndDecelerating = { [weak self] in
            self?.snap()
        }
        delegateProxy = proxy
        sv.delegate = proxy

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
    }

    private func detach() {
        contentOffsetObs?.invalidate()
        contentOffsetObs = nil
        if let sv = trackedScrollView, let proxy = delegateProxy, sv.delegate === proxy {
            sv.delegate = proxy.original
        }
        trackedScrollView = nil
        delegateProxy = nil
    }

    private func snap() {
        let threshold = totalHeight / 2
        searchBarOffset = searchBarOffset < -threshold ? -totalHeight : 0
        onSearchBarOffset?(searchBarOffset)
    }

    deinit { detach() }
}

// MARK: - Forwarding delegate proxy

/// Intercepts the three drag/deceleration lifecycle callbacks we need and forwards
/// everything else to SwiftUI's original internal delegate.
private class ScrollDelegateProxy: NSObject, UIScrollViewDelegate {
    weak var original: UIScrollViewDelegate?
    var onWillBeginDragging: (() -> Void)?
    var onDidEndDragging: ((Bool) -> Void)?
    var onDidEndDecelerating: (() -> Void)?

    init(original: UIScrollViewDelegate?) {
        self.original = original
    }

    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        onWillBeginDragging?()
        original?.scrollViewWillBeginDragging?(scrollView)
    }

    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        onDidEndDragging?(decelerate)
        original?.scrollViewDidEndDragging?(scrollView, willDecelerate: decelerate)
    }

    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        onDidEndDecelerating?()
        original?.scrollViewDidEndDecelerating?(scrollView)
    }

    // Transparent forwarding — all other delegate calls go to SwiftUI's original delegate
    override func responds(to aSelector: Selector!) -> Bool {
        super.responds(to: aSelector) || (original?.responds(to: aSelector) ?? false)
    }

    override func forwardingTarget(for aSelector: Selector!) -> Any? {
        original?.responds(to: aSelector) == true ? original : super.forwardingTarget(for: aSelector)
    }
}
