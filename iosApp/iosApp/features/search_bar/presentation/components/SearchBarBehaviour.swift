//
//  SearchBarBehaviour.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 18/12/25.
//

import Foundation
import ComposeApp

extension Tab {
    var searchBarBehavior: SearchBarBehavior {
        switch self {
        case .home:
            return SearchBarBehavior.scrollReactive
        case .favorites:
            return SearchBarBehavior.scrollReactive
        case .notifications:
            return SearchBarBehavior.hidden
        case .profile:
            return SearchBarBehavior.static
        }
    }
}

extension SearchBarBehavior {
    static func forProductDetail() -> SearchBarBehavior {
        return .scrollReactive
    }
}
