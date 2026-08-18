package org.example.fakeshop_clients.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Single delegate for the "app_prefs" store — DataStore permits only one instance per file name
// per process, so every consumer (install id, session id) must go through this one.
internal val Context.appPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")
