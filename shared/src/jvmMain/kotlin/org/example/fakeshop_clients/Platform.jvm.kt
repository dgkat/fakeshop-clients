package org.example.fakeshop_clients
class JvmPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}
actual fun getPlatform(): Platform {
    return JvmPlatform()
}