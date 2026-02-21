package org.example.fakeshop_clients.core.assets

/**
 * Provides React CDN URLs and SRI integrity hashes based on the current environment.
 *
 * Reads the same `io.ktor.development` system property that Ktor itself uses,
 * so development mode is automatic when running via `./gradlew :web:ssr:run`
 * and production mode is used for deployed builds.
 *
 * React version: 18.3.1
 */
object ReactCdn {
    private val isDevelopment: Boolean =
        System.getProperty("io.ktor.development")?.toBoolean() ?: false

    private const val VERSION = "18.3.1"

    val react: String = if (isDevelopment) {
        "https://unpkg.com/react@$VERSION/umd/react.development.js"
    } else {
        "https://unpkg.com/react@$VERSION/umd/react.production.min.js"
    }

    val reactIntegrity: String = if (isDevelopment) {
        "sha384-hD6/rw4ppMLGNu3tX5cjIb+uRZ7UkRJ6BPkLpg4hAu/6onKUg4lLsHAs9EBPT82L"
    } else {
        "sha384-DGyLxAyjq0f9SPpVevD6IgztCFlnMF6oW/XQGmfe+IsZ8TqEiDrcHkMLKI6fiB/Z"
    }

    val reactDom: String = if (isDevelopment) {
        "https://unpkg.com/react-dom@$VERSION/umd/react-dom.development.js"
    } else {
        "https://unpkg.com/react-dom@$VERSION/umd/react-dom.production.min.js"
    }

    val reactDomIntegrity: String = if (isDevelopment) {
        "sha384-u6aeetuaXnQ38mYT8rp6sbXaQe3NL9t+IBXmnYxwkUI2Hw4bsp2Wvmx4yRQF1uAm"
    } else {
        "sha384-gTGxhz21lVGYNMcdJOyq01Edg0jhn/c22nsx0kyqP0TxaV5WVdsSH1fSDUf5YJj1"
    }
}

/**
 * CDN URLs and SRI integrity hashes for non-React external scripts.
 *
 * HTMX version: 1.9.10
 * React Router DOM version: 6.30.3
 */
object ExternalScripts {
    const val HTMX_SRC =
        "https://unpkg.com/htmx.org@1.9.10/dist/htmx.min.js"
    const val HTMX_INTEGRITY =
        "sha384-D1Kt99CQMDuVetoL1lrYwg5t+9QdHe7NLX/SoJYkXDFfX37iInKRy5xLSi8nO7UC"

    const val REACT_ROUTER_SRC =
        "https://unpkg.com/react-router-dom@6.30.3/dist/umd/react-router-dom.production.min.js"
    const val REACT_ROUTER_INTEGRITY =
        "sha384-WAQzGUwEi6Ve0K316GyxGczPkuX4EHCHcwB0x/C1cuFUPiL9wr8dy9uTgtVm1cNR"
}
