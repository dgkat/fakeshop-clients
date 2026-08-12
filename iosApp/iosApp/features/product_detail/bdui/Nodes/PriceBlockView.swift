//
//  PriceBlockView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct PriceBlockView: View {
    let node: UiNodePriceBlock
    let data: BindData

    var body: some View {
        let price = (node.bind.map { data.resolveDouble(path: $0)?.doubleValue } ?? nil)
        if let price {
            Text(String(format: "$%.2f", price))
                .font(.system(size: 28, weight: .semibold))
                .foregroundColor(FakeShopColors.primary)
        }
    }
}
