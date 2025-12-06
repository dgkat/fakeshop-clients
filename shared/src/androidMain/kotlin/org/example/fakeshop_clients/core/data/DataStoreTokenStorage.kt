package org.example.fakeshop_clients.core.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64
import org.example.fakeshop_clients.core.auth.data.TokenStorage

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "secure_token_storage"
)

class DataStoreTokenStorage(private val context: Context) : TokenStorage {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    @Volatile
    private var cachedAccessToken: String? = null

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        cachedAccessToken = accessToken

        try {
            context.dataStore.edit { preferences ->
                preferences[ACCESS_TOKEN_KEY] = encrypt(accessToken)
                preferences[REFRESH_TOKEN_KEY] = encrypt(refreshToken)
            }
        } catch (e: Exception) {
            throw SecurityException("Failed to save tokens securely", e)
        }
    }

    override suspend fun getAccessToken(): String? {
        return cachedAccessToken ?: try {
            context.dataStore.data
                .map { preferences -> preferences[ACCESS_TOKEN_KEY] }
                .first()
                ?.let { decrypt(it) }
                ?.also { cachedAccessToken = it }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getRefreshToken(): String? {
        return try {
            context.dataStore.data
                .map { preferences -> preferences[REFRESH_TOKEN_KEY] }
                .first()
                ?.let { decrypt(it) }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun clearTokens() {
        cachedAccessToken = null
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }

    private fun getOrCreateKey(): SecretKey {
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
                init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setUserAuthenticationRequired(false)
                        .build()
                )
            }.generateKey()
        }
    }

    private fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv + encrypted, Base64.DEFAULT)
    }

    private fun decrypt(ciphertext: String): String {
        val combined = Base64.decode(ciphertext, Base64.DEFAULT)
        val iv = combined.copyOfRange(0, 12)
        val encrypted = combined.copyOfRange(12, combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }

    companion object {
        private const val KEY_ALIAS = "token_encryption_key"
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}