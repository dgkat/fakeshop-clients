//
//  NavTab.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 1/12/25.
//

import SwiftUI

enum Tab: String, CaseIterable {
    case home = "Home"
    case favorites = "Favorites"
    case notifications = "Notifications"
    case profile = "Profile"
    
    var icon: String {
        switch self {
        case .home: return "house"
        case .favorites: return "heart"
        case .notifications: return "bell"
        case .profile: return "person"
        }
    }
    
    var iconFilled: String {
        switch self {
        case .home: return "house.fill"
        case .favorites: return "heart.fill"
        case .notifications: return "bell.fill"
        case .profile: return "person.fill"
        }
    }
}
