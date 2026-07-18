package org.example.fakeshop_clients.core.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppRouteParserTest {

    // --- Bare canonical paths (the v1 template contract) ---

    @Test
    fun rootPathParsesToHome() {
        assertEquals(AppRoute.Home, AppRouteParser.parse("/"))
    }

    @Test
    fun productPathParsesToProductDetail() {
        assertEquals(
            AppRoute.ProductDetail("socks-123"),
            AppRouteParser.parse("/product/socks-123")
        )
    }

    @Test
    fun tabPathsParseToTabRoutes() {
        assertEquals(AppRoute.Favorites, AppRouteParser.parse("/favorites"))
        assertEquals(AppRoute.Notifications, AppRouteParser.parse("/notifications"))
        assertEquals(AppRoute.Profile, AppRouteParser.parse("/profile"))
    }

    // --- Locale-prefixed paths (web URLs and deeplinks) ---

    @Test
    fun localePrefixedPathsParse() {
        assertEquals(AppRoute.Home, AppRouteParser.parse("/en"))
        assertEquals(AppRoute.Home, AppRouteParser.parse("/es/"))
        assertEquals(
            AppRoute.ProductDetail("socks-123"),
            AppRouteParser.parse("/en/product/socks-123")
        )
        assertEquals(AppRoute.Favorites, AppRouteParser.parse("/es/favorites"))
    }

    // --- Full https URLs (Universal/App Links) ---

    @Test
    fun fullUrlsParse() {
        assertEquals(
            AppRoute.ProductDetail("socks-123"),
            AppRouteParser.parse("https://shop.example.com/en/product/socks-123")
        )
        assertEquals(AppRoute.Home, AppRouteParser.parse("https://shop.example.com"))
        assertEquals(AppRoute.Home, AppRouteParser.parse("https://shop.example.com/"))
        assertEquals(AppRoute.Profile, AppRouteParser.parse("http://localhost:8080/profile"))
    }

    // --- Query and fragment are ignored ---

    @Test
    fun queryAndFragmentAreStripped() {
        assertEquals(
            AppRoute.ProductDetail("socks-123"),
            AppRouteParser.parse("/product/socks-123?utm_source=email#reviews")
        )
        assertEquals(AppRoute.Favorites, AppRouteParser.parse("/favorites?sort=recent"))
    }

    @Test
    fun surroundingWhitespaceIsTrimmed() {
        assertEquals(AppRoute.Favorites, AppRouteParser.parse("  /favorites "))
    }

    // --- Unknown/invalid input ⇒ null (caller must no-op, never crash) ---

    @Test
    fun unknownPathsReturnNull() {
        assertNull(AppRouteParser.parse("/checkout"))
        assertNull(AppRouteParser.parse("/product"))
        assertNull(AppRouteParser.parse("/product/socks-123/reviews"))
        assertNull(AppRouteParser.parse("/en/unknown"))
    }

    @Test
    fun invalidProductIdsReturnNull() {
        assertNull(AppRouteParser.parse("/product/so cks"))
        assertNull(AppRouteParser.parse("/product/socks%20123"))
        assertNull(AppRouteParser.parse("/product/../admin"))
    }

    @Test
    fun nonRootedNonHttpInputReturnsNull() {
        assertNull(AppRouteParser.parse(""))
        assertNull(AppRouteParser.parse("product/socks-123"))
        assertNull(AppRouteParser.parse("mailto:someone@example.com"))
        assertNull(AppRouteParser.parse("fakeshop://product/socks-123"))
    }

    @Test
    fun onlyOneLocaleSegmentIsDropped() {
        assertNull(AppRouteParser.parse("/en/es/product/socks-123"))
    }
}
