//
//  InteractivePopGesture.swift
//  iosApp
//
//  Re-enables the edge swipe-back gesture on screens that hide the navigation bar.
//
//  SwiftUI's NavigationStack is backed by a UINavigationController whose
//  interactivePopGestureRecognizer is tied to the visible back button. Screens that
//  call `.toolbar(.hidden, for: .navigationBar)` (e.g. ProductDetailView) therefore
//  lose the swipe-back gesture. Reinstalling a delegate that allows the recognizer
//  whenever the stack is deeper than its root restores the gesture without adding any
//  custom back handling.
//
//  The delegate lives on a dedicated object rather than on UINavigationController
//  itself: retroactively conforming an imported type to an imported protocol
//  (UIGestureRecognizerDelegate) is undefined if Apple ever adds that conformance,
//  which is exactly what the compiler warns about. A standalone delegate sidesteps it.
//

import UIKit

private final class PopGestureDelegate: NSObject, UIGestureRecognizerDelegate {
    weak var navigationController: UINavigationController?

    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        (navigationController?.viewControllers.count ?? 0) > 1
    }
}

extension UINavigationController {
    private static var popGestureDelegateKey: UInt8 = 0

    open override func viewDidLoad() {
        super.viewDidLoad()

        let delegate = PopGestureDelegate()
        delegate.navigationController = self
        objc_setAssociatedObject(
            self,
            &Self.popGestureDelegateKey,
            delegate,
            .OBJC_ASSOCIATION_RETAIN_NONATOMIC
        )
        interactivePopGestureRecognizer?.delegate = delegate
    }
}
