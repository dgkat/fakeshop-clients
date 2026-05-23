//
//  ImageNodeView.swift
//  iosApp
//

import SwiftUI
import ComposeApp

struct ImageNodeView: View {
    let node: UiNodeImage
    let data: BindData

    var body: some View {
        let url = (node.bind.map { data.resolveString(path: $0) } ?? nil) ?? ""
        // Color.clear drives the layout size (responds purely to proposed width as a 1:1 square).
        // AsyncImage is an overlay so its post-load natural dimensions never escape the frame.
        Color.clear
            .aspectRatio(1, contentMode: .fit)
            .overlay(
                Group {
                    if let u = URL(string: url), !url.isEmpty {
                        AsyncImage(url: u) { image in
                            image.resizable().aspectRatio(contentMode: .fill)
                        } placeholder: {
                            Rectangle().fill(FakeShopColors.surfaceVariant)
                        }
                    } else {
                        Rectangle().fill(FakeShopColors.surfaceVariant)
                    }
                }
            )
            .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

struct ImageGalleryNodeView: View {
    let node: UiNodeImageGallery
    let data: BindData

    var body: some View {
        let items = (node.bind.map { data.resolveStringList(path: $0) } ?? nil) ?? []
        if items.isEmpty {
            Rectangle()
                .fill(FakeShopColors.surfaceVariant)
                .frame(maxWidth: .infinity)
                .frame(height: 120)
                .clipShape(RoundedRectangle(cornerRadius: 8))
        } else {
            HStack(spacing: 4) {
                ForEach(Array(items.enumerated()), id: \.offset) { _, url in
                    AsyncImage(url: URL(string: url)) { image in
                        image.resizable().aspectRatio(contentMode: .fill)
                    } placeholder: {
                        Rectangle().fill(FakeShopColors.surfaceVariant)
                    }
                    .aspectRatio(1, contentMode: .fit)
                    .frame(maxWidth: .infinity)
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                }
            }
        }
    }
}
