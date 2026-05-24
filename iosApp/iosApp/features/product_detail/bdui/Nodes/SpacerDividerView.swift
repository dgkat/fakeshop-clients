//
//  SpacerDividerView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct SpacerNodeView: View {
    let node: UiNodeSpacer

    var body: some View {
        Color.clear.frame(height: spacerHeight(node.size))
    }
}

struct DividerNodeView: View {
    var body: some View {
        Divider()
            .background(FakeShopColors.outline)
    }
}
