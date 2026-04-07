package org.example.fakeshop_clients.features.favorites.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MobileFavoritesCache : FavoritesCache {

    private val _favoritedIds = MutableStateFlow<Set<String>>(emptySet())
    override val favoritedIds: StateFlow<Set<String>> = _favoritedIds.asStateFlow()

    override fun setIds(ids: Set<String>) {
        _favoritedIds.value = ids
    }

    override fun addIds(ids: Set<String>) {
        _favoritedIds.update { it + ids }
    }

    override fun removeId(productId: String) {
        _favoritedIds.update { it - productId }
    }

    override fun toggle(productId: String) {
        _favoritedIds.update {
            if (productId in it) it - productId else it + productId
        }
    }

    override fun clear() {
        _favoritedIds.value = emptySet()
    }
}
