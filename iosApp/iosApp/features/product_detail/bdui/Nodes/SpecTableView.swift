//
//  SpecTableView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct SpecTableView: View {
    let node: UiNodeSpecTable
    let data: BindData

    var body: some View {
        let pairs: [KotlinPair<NSString, NSString>] = (node.bind.map { data.resolveSpecPairs(path: $0) } ?? nil) ?? []
        VStack(alignment: .leading, spacing: 4) {
            ForEach(Array(pairs.enumerated()), id: \.offset) { _, pair in
                HStack {
                    Text((pair.first as String?) ?? "")
                        .font(.system(size: 12))
                        .foregroundColor(FakeShopColors.onSurfaceVariant)
                    Spacer(minLength: 8)
                    Text((pair.second as String?) ?? "")
                        .font(.system(size: 12))
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
