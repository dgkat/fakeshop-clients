package org.example.fakeshop_clients.core.auth.data

import org.example.fakeshop_clients.core.auth.domain.InstallIdProvider
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults

class IosInstallIdProvider : InstallIdProvider {

    override suspend fun get(): String {
        val defaults = NSUserDefaults.standardUserDefaults
        val stored = defaults.stringForKey(INSTALL_ID_KEY)
        if (stored != null) return stored

        val newId = NSUUID().UUIDString
        defaults.setObject(newId, INSTALL_ID_KEY)
        return newId
    }

    companion object {
        private const val INSTALL_ID_KEY = "install_id"
    }
}
