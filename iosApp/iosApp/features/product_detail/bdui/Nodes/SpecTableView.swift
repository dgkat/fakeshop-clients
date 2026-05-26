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
        let items: [SpecItem] = (node.bind.map { data.resolveSpecItems(path: $0) } ?? nil) ?? []
        if !items.isEmpty {
            VStack(alignment: .leading, spacing: 4) {
                ForEach(Array(items.enumerated()), id: \.offset) { _, item in
                    switch onEnum(of: item) {
                    case .group(let g):
                        Text(g.title)
                            .font(.system(size: 12, weight: .semibold))
                            .padding(.top, 8)
                            .padding(.bottom, 2)
                    case .row(let r):
                        HStack {
                            Text(r.label)
                                .font(.system(size: 12))
                                .foregroundColor(FakeShopColors.onSurfaceVariant)
                            Spacer(minLength: 8)
                            Text(r.value)
                                .font(.system(size: 12))
                        }
                    }
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}
