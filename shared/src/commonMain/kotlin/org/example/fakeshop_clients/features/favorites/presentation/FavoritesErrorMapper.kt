package org.example.fakeshop_clients.features.favorites.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

/**
 * Maps a transport-level [NetworkError] to a feature-level [FavoritesError]. Dedicated mapper file
 * (mirrors `ProfileErrorMapper`) so the mapping is reusable and testable rather than inlined in the
 * ViewStore (item 15).
 */
internal fun NetworkError.toFavoritesError(): FavoritesError =
    if (this is NetworkError.HttpError && code == 401) FavoritesError.NotLoggedIn
    else FavoritesError.Network(this)
