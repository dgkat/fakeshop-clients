package org.example.fakeshop_clients.core.interactions.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import org.example.fakeshop_clients.core.data.appPrefsDataStore
import org.example.fakeshop_clients.core.interactions.domain.SessionIdStore
import org.example.fakeshop_clients.core.interactions.domain.StoredSession

class DataStoreSessionIdStore(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : SessionIdStore {

    override suspend fun read(): StoredSession? = withContext(dispatcherProvider.io) {
        val prefs = context.appPrefsDataStore.data.first()
        val id = prefs[SESSION_ID_KEY] ?: return@withContext null
        StoredSession(id = id, lastTouchedMillis = prefs[SESSION_TOUCHED_KEY])
    }

    override suspend fun write(id: String, lastTouchedMillis: Long) {
        withContext(dispatcherProvider.io) {
            context.appPrefsDataStore.edit {
                it[SESSION_ID_KEY] = id
                it[SESSION_TOUCHED_KEY] = lastTouchedMillis
            }
        }
    }

    override suspend fun clear() {
        withContext(dispatcherProvider.io) {
            context.appPrefsDataStore.edit {
                it.remove(SESSION_ID_KEY)
                it.remove(SESSION_TOUCHED_KEY)
            }
        }
    }

    companion object {
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
        private val SESSION_TOUCHED_KEY = longPreferencesKey("session_last_touched")
    }
}
