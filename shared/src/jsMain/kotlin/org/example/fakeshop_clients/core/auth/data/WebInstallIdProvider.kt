package org.example.fakeshop_clients.core.auth.data

import kotlinx.browser.localStorage
import org.example.fakeshop_clients.core.auth.domain.InstallIdProvider

class WebInstallIdProvider : InstallIdProvider {

    override suspend fun get(): String {
        val stored = localStorage.getItem(INSTALL_ID_KEY)
        if (stored != null) return stored

        val newId = js("crypto.randomUUID()").unsafeCast<String>()
        localStorage.setItem(INSTALL_ID_KEY, newId)
        return newId
    }

    companion object {
        private const val INSTALL_ID_KEY = "install_id"
    }
}
