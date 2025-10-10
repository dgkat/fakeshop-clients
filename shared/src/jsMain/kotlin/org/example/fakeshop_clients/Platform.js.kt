package org.example.fakeshop_clients

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()