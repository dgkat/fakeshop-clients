package org.example.fakeshop_clients.features.favorites.data

import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WebFavoritesCache : FavoritesCache {

    private val _favoritedIds = MutableStateFlow<Set<String>>(emptySet())
    override val favoritedIds: StateFlow<Set<String>> = _favoritedIds.asStateFlow()

    init {
        val stored = window.sessionStorage.getItem(SESSION_STORAGE_KEY)
        if (!stored.isNullOrEmpty()) {
            val ids = stored.split(",").filter { it.isNotEmpty() }.toSet()
            if (ids.isNotEmpty()) {
                _favoritedIds.value = ids
            }
        }
    }

    override fun setIds(ids: Set<String>) {
        _favoritedIds.value = ids
        persistToSessionStorage(ids)
    }

    override fun addIds(ids: Set<String>) {
        _favoritedIds.update { it + ids }
        persistToSessionStorage(_favoritedIds.value)
    }

    override fun removeId(productId: String) {
        _favoritedIds.update { it - productId }
        persistToSessionStorage(_favoritedIds.value)
    }

    override fun toggle(productId: String) {
        _favoritedIds.update {
            if (productId in it) it - productId else it + productId
        }
        persistToSessionStorage(_favoritedIds.value)
    }

    override fun clear() {
        _favoritedIds.value = emptySet()
        persistToSessionStorage(emptySet())
    }

    private fun persistToSessionStorage(ids: Set<String>) {
        if (ids.isEmpty()) {
            window.sessionStorage.removeItem(SESSION_STORAGE_KEY)
        } else {
            window.sessionStorage.setItem(SESSION_STORAGE_KEY, ids.joinToString(","))
        }
    }

    companion object {
        private const val SESSION_STORAGE_KEY = "favorited_ids"
    }
}
