package org.example.fakeshop_clients.core.navigation

object AppRouteParser {

    private val productIdRegex = Regex("^[A-Za-z0-9-]+$")

    fun parse(input: String): AppRoute? {
        val path = stripSchemeAndHost(input.trim()) ?: return null
        val segments = path
            .substringBefore('?')
            .substringBefore('#')
            .split('/')
            .filter { it.isNotEmpty() }
            .let(::dropLocaleSegment)

        return when {
            segments.isEmpty() -> AppRoute.Home
            segments.size == 1 && segments[0] == "favorites" -> AppRoute.Favorites
            segments.size == 1 && segments[0] == "notifications" -> AppRoute.Notifications
            segments.size == 1 && segments[0] == "profile" -> AppRoute.Profile
            segments.size == 2 && segments[0] == "product" &&
                productIdRegex.matches(segments[1]) -> AppRoute.ProductDetail(segments[1])
            else -> null
        }
    }

    /** Returns a rooted path, or null if the input is neither a rooted path nor an http(s) URL. */
    private fun stripSchemeAndHost(input: String): String? {
        if (input.startsWith("/")) return input
        if (input.startsWith("https://") || input.startsWith("http://")) {
            val afterScheme = input.substringAfter("://")
            val slash = afterScheme.indexOf('/')
            return if (slash == -1) "/" else afterScheme.substring(slash)
        }
        return null
    }

    /** Web URLs and deeplinks may carry a 2-letter locale prefix (`/en/...`) — drop it. */
    private fun dropLocaleSegment(segments: List<String>): List<String> {
        val first = segments.firstOrNull() ?: return segments
        return if (first.length == 2 && first.all { it.isLetter() }) segments.drop(1) else segments
    }
}
