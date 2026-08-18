package org.example.fakeshop_clients.core.interactions.domain

/**
 * Where the user was when they acted.
 *
 * When a view results from tapping an item in a list, send the *originating* surface — the list the
 * user tapped — not the screen they land on. A product opened from a home shelf reports
 * [HOME_SHELF], never [PRODUCT_SCREEN]. Getting this wrong collapses the column to a single value
 * with no error anywhere to notice it.
 *
 * [PRODUCT_SCREEN] is correct only for views that did not originate in a list: a deeplink, a BDUI
 * `navigate`, an address-bar hit, or a back-navigation that reloads.
 *
 * [wireValue] is explicit rather than derived from `name` so a Kotlin-side rename cannot silently
 * change the wire contract and orphan historical rows from their analytics queries.
 */
enum class InteractionSurface(val wireValue: String) {
    PRODUCT_SCREEN("PRODUCT_SCREEN"),
    HOME_SHELF("HOME_SHELF"),
    SEARCH("SEARCH"),
    CATEGORY("CATEGORY"),
    RECOMMENDATIONS("RECOMMENDATIONS");

    companion object {
        /** Unrecognised input degrades to [PRODUCT_SCREEN] — an old bookmark must still render. */
        fun fromWireValue(value: String?): InteractionSurface =
            entries.firstOrNull { it.wireValue.equals(value, ignoreCase = true) } ?: PRODUCT_SCREEN
    }
}
