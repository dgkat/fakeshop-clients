package org.example.fakeshop_clients.core.notifications

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.pendingDeviceTokenStore: DataStore<Preferences> by preferencesDataStore(
    name = "pending_device_token"
)

class AndroidPendingDeviceTokenCache(
    private val context: Context
) {

    suspend fun save(token: String) {
        context.pendingDeviceTokenStore.edit { it[PENDING_TOKEN_KEY] = token }
    }

    suspend fun consume(): String? {
        val token = context.pendingDeviceTokenStore.data
            .map { it[PENDING_TOKEN_KEY] }
            .first()
        if (token != null) {
            context.pendingDeviceTokenStore.edit { it.remove(PENDING_TOKEN_KEY) }
        }
        return token
    }

    private companion object {
        val PENDING_TOKEN_KEY = stringPreferencesKey("pending_fcm_token")
    }
}
