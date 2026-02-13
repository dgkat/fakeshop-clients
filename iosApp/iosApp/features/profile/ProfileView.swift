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
            
            Text(String(localized: "tab_profile"))
                .font(.largeTitle)
                .fontWeight(.bold)

            Text(String(localized: "profile_subtitle"))
                .font(.body)
                .foregroundColor(FakeShopColors.onSurfaceVariant)
        }
        .navigationTitle(String(localized: "tab_profile"))
    }
}
