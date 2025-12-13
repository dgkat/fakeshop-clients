//
//  ComposeProfileView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 11/12/25.
//

import SwiftUI
import UIKit
import ComposeApp

struct ComposeProfileView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return ProfileViewControllerKt.ProfileViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // No updates needed for this use case
    }
}
