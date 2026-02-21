package org.example.fakeshop_clients.core.assets

/**
 * Provides React CDN URLs based on the current environment.
 *
 * Reads the same `io.ktor.development` system property that Ktor itself uses,
 * so development mode is automatic when running via `./gradlew :web:ssr:run`
 * and production mode is used for deployed builds.
 */
object ReactCdn {
    private val isDevelopment: Boolean =
        System.getProperty("io.ktor.development")?.toBoolean() ?: false

    val react: String = if (isDevelopment) {
        "https://unpkg.com/react@18/umd/react.development.js"
    } else {
        "https://unpkg.com/react@18/umd/react.production.min.js"
    }

    val reactDom: String = if (isDevelopment) {
        "https://unpkg.com/react-dom@18/umd/react-dom.development.js"
    } else {
        "https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"
    }
}
