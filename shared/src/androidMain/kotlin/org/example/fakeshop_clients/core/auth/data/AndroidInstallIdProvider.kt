package org.example.fakeshop_clients.core.auth.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.example.fakeshop_clients.core.auth.domain.InstallIdProvider
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import java.util.UUID

private val Context.appPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class AndroidInstallIdProvider(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : InstallIdProvider {

    override suspend fun get(): String = withContext(dispatcherProvider.io) {
        val stored = context.appPrefsDataStore.data
            .map { it[INSTALL_ID_KEY] }
            .first()

        if (stored != null) return@withContext stored

        val newId = UUID.randomUUID().toString()
        context.appPrefsDataStore.edit { it[INSTALL_ID_KEY] = newId }
        newId
    }

    companion object {
        private val INSTALL_ID_KEY = stringPreferencesKey("install_id")
    }
}
