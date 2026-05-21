//
//  ScrollableVStack.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 18/12/25.
//

import SwiftUI

struct ScrollableVStack<Content: View>: UIViewControllerRepresentable {
    let content: Content
    let onScroll: (CGFloat) -> Void

    init(onScroll: @escaping (CGFloat) -> Void, @ViewBuilder content: () -> Content) {
        self.onScroll = onScroll
        self.content = content()
    }

    func makeUIViewController(context: Context) -> UIScrollViewController<Content> {
        UIScrollViewController(rootView: content, onScroll: onScroll)
    }

    func updateUIViewController(_ uiViewController: UIScrollViewController<Content>, context: Context) {
        uiViewController.hostingController.rootView = content
        // Force SwiftUI to commit the new layout before measuring.
        // Without this, sizeThatFits races against SwiftUI's async layout
        // scheduler and returns the previous content's height.
        uiViewController.hostingController.view.setNeedsLayout()
        uiViewController.hostingController.view.layoutIfNeeded()
        uiViewController.updateContentSize()
    }
}

class UIScrollViewController<Content: View>: UIViewController, UIScrollViewDelegate {
    let scrollView = UIScrollView()
    let hostingController: UIHostingController<Content>
    let onScroll: (CGFloat) -> Void

    private var lastContentOffsetY: CGFloat = 0
    private var searchBarOffset: CGFloat = 0
    private var isDragging: Bool = false

    private var searchBarHeight: CGFloat { 58 }

    private var statusBarHeight: CGFloat {
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let window = windowScene.windows.first {
            return window.safeAreaInsets.top
        }
        return 0
    }

    private var totalHeight: CGFloat { searchBarHeight + statusBarHeight }

    init(rootView: Content, onScroll: @escaping (CGFloat) -> Void) {
        self.hostingController = UIHostingController(rootView: rootView)
        self.onScroll = onScroll
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        scrollView.delegate = self
        scrollView.frame = view.bounds
        scrollView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        scrollView.showsVerticalScrollIndicator = true
        scrollView.alwaysBounceVertical = true
        scrollView.backgroundColor = .clear
        scrollView.contentInsetAdjustmentBehavior = .never
        view.backgroundColor = .clear
        view.addSubview(scrollView)

        addChild(hostingController)
        hostingController.view.backgroundColor = .clear
        // Prevent UIKit from injecting safe area insets into the hosted SwiftUI content.
        // On screens with a hidden nav bar (e.g. ProductDetailView), the view controller
        // receives a non-zero safeAreaInsets.top which UIHostingController would otherwise
        // propagate as implicit padding, clipping the top/bottom content at rest.
        hostingController.view.insetsLayoutMarginsFromSafeArea = false
        scrollView.addSubview(hostingController.view)
        hostingController.didMove(toParent: self)

        hostingController.view.frame = CGRect(x: 0, y: 0, width: scrollView.bounds.width, height: 0)
        updateContentSize()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateContentSize()
    }

    func updateContentSize() {
        guard scrollView.bounds.width > 0 else { return }

        let targetSize = CGSize(width: scrollView.bounds.width, height: UIView.layoutFittingCompressedSize.height)
        let newSize = hostingController.view.sizeThatFits(targetSize)

        guard newSize.height != scrollView.contentSize.height else { return }

        hostingController.view.frame = CGRect(x: 0, y: 0, width: scrollView.bounds.width, height: newSize.height)
        scrollView.contentSize = CGSize(width: scrollView.bounds.width, height: newSize.height)
    }

    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        isDragging = true
        lastContentOffsetY = scrollView.contentOffset.y
    }

    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if !decelerate {
            isDragging = false
            snapSearchBar()
        }
    }

    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        isDragging = false
        snapSearchBar()
    }

    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        guard isDragging else { return }

        let currentOffsetY = scrollView.contentOffset.y

        guard currentOffsetY >= 0 else { return }

        let maxOffsetY = max(0, scrollView.contentSize.height - scrollView.bounds.height)
        guard currentOffsetY <= maxOffsetY else { return }

        let delta = currentOffsetY - lastContentOffsetY
        searchBarOffset -= delta
        searchBarOffset = max(-totalHeight, min(0, searchBarOffset))
        onScroll(searchBarOffset)
        lastContentOffsetY = currentOffsetY
    }

    private func snapSearchBar() {
        let threshold = totalHeight / 2
        searchBarOffset = searchBarOffset < -threshold ? -totalHeight : 0
        onScroll(searchBarOffset)
    }
}
