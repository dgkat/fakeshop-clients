package org.example.fakeshop_clients.features.profile.presentation

import androidx.compose.ui.window.ComposeUIViewController
import org.example.fakeshop_clients.features.core.di.ensureComposeKoinModulesLoaded
import org.example.fakeshop_clients.ui.theme.FakeShopTheme
import platform.UIKit.UIViewController

fun ProfileViewController(): UIViewController {
    ensureComposeKoinModulesLoaded()
    return ComposeUIViewController {
        FakeShopTheme {
            ProfileScreen()
        }
    }
}