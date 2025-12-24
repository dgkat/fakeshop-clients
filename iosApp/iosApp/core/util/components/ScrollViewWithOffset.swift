//
//  ScrollViewWithOffset.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 18/12/25.
//


import SwiftUI

struct ScrollViewWithOffset<Content: View>: View {
    let axes: Axis.Set
    let showsIndicators: Bool
    let offsetChanged: (CGFloat) -> Void
    let content: Content
    
    init(
        axes: Axis.Set = .vertical,
        showsIndicators: Bool = true,
        offsetChanged: @escaping (CGFloat) -> Void = { _ in },
        @ViewBuilder content: () -> Content
    ) {
        self.axes = axes
        self.showsIndicators = showsIndicators
        self.offsetChanged = offsetChanged
        self.content = content()
    }
    
    var body: some View {
        SwiftUI.ScrollView(axes, showsIndicators: showsIndicators) {
            GeometryReader { geometry in
                Color.clear.preference(
                    key: ScrollOffsetPreferenceKey.self,
                    value: geometry.frame(in: .named("scrollView")).origin.y
                )
            }
            .frame(width: 0, height: 0)
            
            content
        }
        .coordinateSpace(name: "scrollView")
        .onPreferenceChange(ScrollOffsetPreferenceKey.self, perform: offsetChanged)
    }
}
