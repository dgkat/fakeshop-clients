//
//  KoinHelper.swift
//  iosApp
//
//  Created by Dimitrios Katoudis on 6/11/25.
//

import Foundation
import Shared

class KoinHelper {
    // Singleton instance
    static let shared = KoinHelper()
    
    // Reference to Kotlin helper
    let iosHelper: IOSKoinHelper
    
    private init() {
        // Initialize Koin (happens once when singleton is first accessed)
        _ = IosModuleKt.doInitKoinIos()
        
        // Create Kotlin helper
        self.iosHelper = IOSKoinHelper()
        
        print("✅ Koin initialized successfully")
    }
}
