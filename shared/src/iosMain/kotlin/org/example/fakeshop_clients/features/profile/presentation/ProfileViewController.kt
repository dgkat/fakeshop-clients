package org.example.fakeshop_clients.features.profile.presentation

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun ProfileViewController(): UIViewController {
    return ComposeUIViewController {
        ProfileScreen()
    }
}