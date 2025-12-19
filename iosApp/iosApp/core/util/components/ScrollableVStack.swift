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
        let vc = UIScrollViewController(rootView: content, onScroll: onScroll)
        return vc
    }
    
    func updateUIViewController(_ uiViewController: UIScrollViewController<Content>, context: Context) {
        uiViewController.hostingController.rootView = content
    }
}

class UIScrollViewController<Content: View>: UIViewController, UIScrollViewDelegate {
    let scrollView = UIScrollView()
    let hostingController: UIHostingController<Content>
    let onScroll: (CGFloat) -> Void
    
    private var lastContentOffsetY: CGFloat = 0
    private var searchBarOffset: CGFloat = 0
    private var isDragging: Bool = false
    
    private var searchBarHeight: CGFloat {
        return 58
    }
    
    private var statusBarHeight: CGFloat {
        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let window = windowScene.windows.first {
            return window.safeAreaInsets.top
        }
        return 0
    }
    
    private var totalHeight: CGFloat {
        return searchBarHeight + statusBarHeight
    }
    
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
        view.addSubview(scrollView)
        
        addChild(hostingController)
        scrollView.addSubview(hostingController.view)
        hostingController.didMove(toParent: self)
        
        hostingController.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            hostingController.view.topAnchor.constraint(equalTo: scrollView.topAnchor),
            hostingController.view.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor),
            hostingController.view.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor),
            hostingController.view.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            hostingController.view.widthAnchor.constraint(equalTo: scrollView.widthAnchor)
        ])
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
        
        guard currentOffsetY >= 0 else {
            return
        }
        
        let maxOffsetY = max(0, scrollView.contentSize.height - scrollView.bounds.height)
        guard currentOffsetY <= maxOffsetY else {
            return
        }
        
        let delta = currentOffsetY - lastContentOffsetY
        
        let total = totalHeight
        
        searchBarOffset -= delta
        searchBarOffset = max(-total, min(0, searchBarOffset))
        
        onScroll(searchBarOffset)
        lastContentOffsetY = currentOffsetY
    }
    
    private func snapSearchBar() {
        let total = totalHeight
        let threshold = total / 2
        
        if searchBarOffset < -threshold {
            searchBarOffset = -total
        } else {
            searchBarOffset = 0
        }
        
        onScroll(searchBarOffset)
    }
}
