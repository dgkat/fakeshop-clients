package org.example.fakeshop_clients.core.interactions.data

import org.example.fakeshop_clients.core.interactions.domain.SessionIdStore
import org.example.fakeshop_clients.core.interactions.domain.StoredSession
import platform.Foundation.NSUserDefaults

class UserDefaultsSessionIdStore : SessionIdStore {

    private val defaults: NSUserDefaults get() = NSUserDefaults.standardUserDefaults

    override suspend fun read(): StoredSession? {
        val id = defaults.stringForKey(SESSION_ID_KEY) ?: return null
        // objectForKey distinguishes "never written" from a legitimately stored 0.
        val touched = defaults.objectForKey(SESSION_TOUCHED_KEY)
            ?.let { defaults.doubleForKey(SESSION_TOUCHED_KEY).toLong() }
        return StoredSession(id = id, lastTouchedMillis = touched)
    }

    override suspend fun write(id: String, lastTouchedMillis: Long) {
        defaults.setObject(id, SESSION_ID_KEY)
        defaults.setDouble(lastTouchedMillis.toDouble(), SESSION_TOUCHED_KEY)
    }

    override suspend fun clear() {
        defaults.removeObjectForKey(SESSION_ID_KEY)
        defaults.removeObjectForKey(SESSION_TOUCHED_KEY)
    }

    companion object {
        private const val SESSION_ID_KEY = "session_id"
        private const val SESSION_TOUCHED_KEY = "session_last_touched"
    }
}
