package org.example.fakeshop_clients.core.data

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.withContext
import org.example.fakeshop_clients.core.auth.data.TokenStorage
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.StorageError
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.darwin.OSStatus
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import kotlin.concurrent.Volatile

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class KeychainTokenStorage(
    private val dispatcherProvider: DispatcherProvider
) : TokenStorage {

    @Volatile
    private var cachedAccessToken: String? = null

    override suspend fun saveTokens(accessToken: String, refreshToken: String): Result<Unit, StorageError> {
        cachedAccessToken = accessToken
        return withContext(dispatcherProvider.io) {
            writeTokens(accessToken, refreshToken)
        }
    }

    override suspend fun replaceTokens(accessToken: String, refreshToken: String): Result<Unit, StorageError> {
        cachedAccessToken = accessToken
        return withContext(dispatcherProvider.io) {
            writeTokens(accessToken, refreshToken)
        }
    }

    private fun writeTokens(accessToken: String, refreshToken: String): Result<Unit, StorageError> {
        return try {
            val accessStatus = saveToKeychain(ACCESS_TOKEN_KEY, accessToken)
            val refreshStatus = saveToKeychain(REFRESH_TOKEN_KEY, refreshToken)
            if (accessStatus == errSecSuccess && refreshStatus == errSecSuccess) {
                Result.Success(Unit)
            } else {
                val failing = if (accessStatus != errSecSuccess) accessStatus else refreshStatus
                Result.Error(StorageError.WriteFailed("Failed to save tokens to keychain (OSStatus=$failing)"))
            }
        } catch (e: Exception) {
            Result.Error(StorageError.WriteFailed(e.message ?: "Keychain write failed", e))
        }
    }

    override suspend fun getAccessToken(): String? {
        return cachedAccessToken ?: withContext(dispatcherProvider.io) {
            readFromKeychain(ACCESS_TOKEN_KEY)
                ?.also { cachedAccessToken = it }
        }
    }

    override suspend fun getRefreshToken(): String? {
        return withContext(dispatcherProvider.io) {
            readFromKeychain(REFRESH_TOKEN_KEY)
        }
    }

    override suspend fun clearTokens() {
        cachedAccessToken = null
        withContext(dispatcherProvider.io) {
            deleteFromKeychain(ACCESS_TOKEN_KEY)
            deleteFromKeychain(REFRESH_TOKEN_KEY)
        }
    }

    private fun saveToKeychain(key: String, value: String): OSStatus {
        deleteFromKeychain(key)

        val valueData = value.toNSData()

        val queryDict = CFDictionaryCreateMutable(
            null,
            5,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr
        )

        val cfService = CFBridgingRetain(SERVICE)
        val cfAccount = CFBridgingRetain(key)
        val cfValue = CFBridgingRetain(valueData)

        CFDictionaryAddValue(queryDict, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(queryDict, kSecAttrService, cfService)
        CFDictionaryAddValue(queryDict, kSecAttrAccount, cfAccount)
        CFDictionaryAddValue(queryDict, kSecValueData, cfValue)
        CFDictionaryAddValue(queryDict, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)

        val status = SecItemAdd(queryDict, null)

        CFRelease(cfService)
        CFRelease(cfAccount)
        CFRelease(cfValue)
        CFRelease(queryDict)

        return status
    }

    private fun readFromKeychain(key: String): String? {
        val queryDict = CFDictionaryCreateMutable(
            null,
            5,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr
        )

        val cfService = CFBridgingRetain(SERVICE)
        val cfAccount = CFBridgingRetain(key)

        CFDictionaryAddValue(queryDict, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(queryDict, kSecAttrService, cfService)
        CFDictionaryAddValue(queryDict, kSecAttrAccount, cfAccount)
        CFDictionaryAddValue(queryDict, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(queryDict, kSecMatchLimit, kSecMatchLimitOne)

        memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(queryDict, result.ptr)

            CFRelease(cfService)
            CFRelease(cfAccount)
            CFRelease(queryDict)

            if (status == errSecSuccess) {
                val dataRef = result.value
                if (dataRef != null) {
                    val nsData = CFBridgingRelease(dataRef) as? NSData
                    return nsData?.toKotlinString()
                }
            }

            return null
        }
    }

    private fun deleteFromKeychain(key: String) {
        val queryDict = CFDictionaryCreateMutable(
            null,
            3,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr
        )

        val cfService = CFBridgingRetain(SERVICE)
        val cfAccount = CFBridgingRetain(key)

        CFDictionaryAddValue(queryDict, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(queryDict, kSecAttrService, cfService)
        CFDictionaryAddValue(queryDict, kSecAttrAccount, cfAccount)

        SecItemDelete(queryDict)

        CFRelease(cfService)
        CFRelease(cfAccount)
        CFRelease(queryDict)
    }

    // Extension functions for type conversion
    private fun String.toNSData(): NSData {
        return NSString.create(string = this)
            .dataUsingEncoding(NSUTF8StringEncoding)
            ?: throw IllegalArgumentException("Failed to encode string")
    }

    private fun NSData.toKotlinString(): String {
        return NSString.create(this, NSUTF8StringEncoding)?.toString()
            ?: throw IllegalArgumentException("Failed to decode string")
    }

    companion object {
        private const val SERVICE = "org.example.fakeshop-clients.auth"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}