package org.example.fakeshop_clients.features.favorites.data

import kotlinx.coroutines.flow.StateFlow

interface FavoritesCache {
    val favoritedIds: StateFlow<Set<String>>
    fun setIds(ids: Set<String>)
    fun addIds(ids: Set<String>)
    fun removeId(productId: String)
    fun toggle(productId: String)
    fun clear()
}
