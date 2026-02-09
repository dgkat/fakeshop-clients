//
//  ProfileView.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//
import SwiftUI

struct ProfileView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "person.fill")
                .font(.system(size: 64))
                .foregroundColor(FakeShopColors.primary)
            
            Text("Profile")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text("Manage your account")
                .font(.body)
                .foregroundColor(.secondary)
        }
        .navigationTitle("Profile")
    }
}
