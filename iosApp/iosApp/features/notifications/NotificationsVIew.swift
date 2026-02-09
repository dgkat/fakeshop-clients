//
//  NotificationsVIew.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//
import SwiftUI

struct NotificationsView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "bell.fill")
                .font(.system(size: 64))
                .foregroundColor(FakeShopColors.primary)
            
            Text("Notifications")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text("Stay tuned for updates")
                .font(.body)
                .foregroundColor(.secondary)
        }
        .navigationTitle("Notifications")
    }
}
