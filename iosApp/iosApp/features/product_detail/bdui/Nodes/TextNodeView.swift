//
//  TextNodeView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct TextNodeView: View {
    let node: UiNodeText
    let data: BindData

    var body: some View {
        let value = (node.bind.map { data.resolveString(path: $0) } ?? nil)
        if let value, !value.isEmpty {
            Text(value)
                .font(bduiFont(for: node.style))
                .multilineTextAlignment(textAlignment(for: node.alignment))
                .frame(maxWidth: .infinity, alignment: alignmentForFrame(node.alignment))
                .fixedSize(horizontal: false, vertical: true)
        }
    }

    private func alignmentForFrame(_ a: NodeAlignment?) -> Alignment {
        switch a {
        case .center?: return .center
        case .end?: return .trailing
        default: return .leading
        }
    }
}
