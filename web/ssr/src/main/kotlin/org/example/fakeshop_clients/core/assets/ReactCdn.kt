package org.example.fakeshop_clients.core.assets

/**
 * CDN URLs and SRI integrity hashes for external scripts loaded from CDN.
 *
 * React and ReactDOM are NOT loaded from CDN — React 19 removed UMD builds,
 * so React is bundled by webpack instead.
 *
 * HTMX version: 1.9.10
 * To update HTMX and regenerate hashes:
 *   ./gradlew :web:ssr:updateCdnHashes -PhtmxVersion=X.Y.Z
 */
object ExternalScripts {
    const val HTMX_SRC =
        "https://unpkg.com/htmx.org@1.9.10/dist/htmx.min.js"
    const val HTMX_INTEGRITY =
        "sha384-D1Kt99CQMDuVetoL1lrYwg5t+9QdHe7NLX/SoJYkXDFfX37iInKRy5xLSi8nO7UC"
}
