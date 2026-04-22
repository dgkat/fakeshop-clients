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
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
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

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        cachedAccessToken = accessToken
        withContext(dispatcherProvider.io) {
            saveToKeychain(ACCESS_TOKEN_KEY, accessToken)
            saveToKeychain(REFRESH_TOKEN_KEY, refreshToken)
        }
    }

    override suspend fun replaceTokens(accessToken: String, refreshToken: String) {
        cachedAccessToken = accessToken
        withContext(dispatcherProvider.io) {
            saveToKeychain(ACCESS_TOKEN_KEY, accessToken)
            saveToKeychain(REFRESH_TOKEN_KEY, refreshToken)
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

    private fun saveToKeychain(key: String, value: String) {
        deleteFromKeychain(key)

        val valueData = value.toNSData()

        val queryDict = CFDictionaryCreateMutable(
            null,
            4,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr
        )

        CFDictionaryAddValue(queryDict, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(queryDict, kSecAttrAccount, CFBridgingRetain(key))
        CFDictionaryAddValue(queryDict, kSecValueData, CFBridgingRetain(valueData))
        CFDictionaryAddValue(queryDict, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)

        val status = SecItemAdd(queryDict, null)
        CFRelease(queryDict)

        if (status != errSecSuccess) {
            throw SecurityException("Failed to save to keychain. Status: $status")
        }
    }

    private fun readFromKeychain(key: String): String? {
        val queryDict = CFDictionaryCreateMutable(
            null,
            4,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr
        )

        CFDictionaryAddValue(queryDict, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(queryDict, kSecAttrAccount, CFBridgingRetain(key))
        CFDictionaryAddValue(queryDict, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(queryDict, kSecMatchLimit, kSecMatchLimitOne)

        memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(queryDict, result.ptr)

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
            2,
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr
        )

        CFDictionaryAddValue(queryDict, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(queryDict, kSecAttrAccount, CFBridgingRetain(key))

        SecItemDelete(queryDict)
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
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}

class SecurityException(message: String) : Exception(message)