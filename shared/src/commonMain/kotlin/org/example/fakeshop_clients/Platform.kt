package org.example.fakeshop_clients

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform