package org.example.fakeshop_clients.features.search.presentation

enum class SearchBarBehavior {
    STATIC,           // Always visible
    SCROLL_REACTIVE,  // Hides on scroll down, shows on scroll up
    HIDDEN            // Completely hidden
}