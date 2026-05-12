//
//  BduiBodySection.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct BduiBodySection: View {
    let state: BduiBodyState
    let onAction: BduiActionHandler

    var body: some View {
        switch onEnum(of: state) {
        case .loading:
            HStack(spacing: 8) {
                ProgressView().scaleEffect(0.8)
                Text("Loading…")
                    .font(.body)
                    .foregroundColor(FakeShopColors.onSurfaceVariant)
            }
        case .error(let errState):
            HStack(spacing: 8) {
                Image(systemName: "exclamationmark.triangle")
                    .foregroundColor(FakeShopColors.error)
                Text(errorMessage(errState.error))
                    .font(.body)
                    .foregroundColor(FakeShopColors.onErrorContainer)
            }
            .padding(.vertical, 8)
            .padding(.horizontal, 12)
            .background(FakeShopColors.errorContainer)
            .cornerRadius(8)
        case .ready(let ready):
            BduiNodeView(
                node: ready.template.root,
                data: BindData(json: ready.bindData),
                onAction: onAction
            )
        }
    }

    private func errorMessage(_ error: BduiError) -> String {
        switch onEnum(of: error) {
        case .network: return "Couldn't load product details"
        case .notFound: return "Product layout not found"
        case .parseError: return "Unsupported layout"
        }
    }
}
