//
//  KoinHelper.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 6/11/25.
//

import Foundation
import ComposeApp

class KoinHelper {
    // Singleton instance
    static let shared = KoinHelper()
    
    // Reference to Kotlin helper
    let iosHelper: IOSKoinHelper
    
    private init() {
        // Initialize Koin (happens once when singleton is first accessed)
        #if DEBUG
        let baseUrl = "http://localhost:8080"
        let isDebug = true
        #else
        let baseUrl = "https://api.dgkat.com"
        let isDebug = false
        #endif
        _ = IosModuleKt.doInitKoinIos(baseUrl: baseUrl, isDebug: isDebug)

        // Create Kotlin helper
        self.iosHelper = IOSKoinHelper()

        print("✅ Koin initialized successfully")
    }
}
