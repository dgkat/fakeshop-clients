package org.example.fakeshop_clients.core.data

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.example.fakeshop_clients.core.concurrency.IosDispatcherProvider
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.StorageError

/**
 * Runtime coverage for [KeychainTokenStorage] (item 3 + 7).
 *
 * **Two layers of signal here:**
 * 1. *Always* exercised, even in CI: every `CFBridgingRetain` / `CFRelease` in save/read/delete runs
 *    regardless of whether the Keychain is reachable. An over-release regression (the failure mode
 *    of the item-7 fix) would crash the test process here — so a clean run proves the CoreFoundation
 *    memory management is balanced.
 * 2. Round-trip save→read→replace→clear correctness — only assertable when a real Keychain is
 *    available. A bare Kotlin/Native XCTest bundle has **no app host**, so `SecItemAdd` returns
 *    `errSecNotAvailable (-25291)` (or `errSecMissingEntitlement -34018`). When that happens we skip
 *    the round-trip assertions rather than fail. To actually run layer 2, host these in an app
 *    target (or verify the leak fix manually with Instruments → Leaks/Allocations).
 *
 * Reads use a fresh instance so the in-memory `cachedAccessToken` is bypassed and the value genuinely
 * comes back out of the Keychain.
 */
class KeychainTokenStorageTest {

    private fun newStorage() = KeychainTokenStorage(IosDispatcherProvider())

    // -25291 errSecNotAvailable / -34018 errSecMissingEntitlement: no Keychain in this test bundle.
    private fun Result<Unit, StorageError>.keychainUnavailable(): Boolean {
        val message = (this as? Result.Error)?.error
            ?.let { (it as? StorageError.WriteFailed)?.message }
            ?: return false
        return message.contains("-25291") || message.contains("-34018")
    }

    @BeforeTest
    fun clearBefore() = runTest { newStorage().clearTokens() }

    @AfterTest
    fun clearAfter() = runTest { newStorage().clearTokens() }

    @Test
    fun saveThenReadRoundTrips() = runTest {
        val result = newStorage().saveTokens("access-1", "refresh-1")
        if (result.keychainUnavailable()) {
            println("SKIP saveThenReadRoundTrips: Keychain unavailable in test bundle ($result)")
            return@runTest
        }
        assertIs<Result.Success<Unit>>(result)

        val reader = newStorage()
        assertEquals("access-1", reader.getAccessToken())
        assertEquals("refresh-1", reader.getRefreshToken())
    }

    @Test
    fun replaceOverwritesExistingTokens() = runTest {
        val first = newStorage().saveTokens("access-1", "refresh-1")
        if (first.keychainUnavailable()) {
            println("SKIP replaceOverwritesExistingTokens: Keychain unavailable in test bundle")
            return@runTest
        }
        assertIs<Result.Success<Unit>>(newStorage().replaceTokens("access-2", "refresh-2"))

        val reader = newStorage()
        assertEquals("access-2", reader.getAccessToken())
        assertEquals("refresh-2", reader.getRefreshToken())
    }

    @Test
    fun clearRemovesTokens() = runTest {
        val saved = newStorage().saveTokens("access-1", "refresh-1")
        if (saved.keychainUnavailable()) {
            println("SKIP clearRemovesTokens: Keychain unavailable in test bundle")
            return@runTest
        }
        newStorage().clearTokens()

        val reader = newStorage()
        assertNull(reader.getAccessToken())
        assertNull(reader.getRefreshToken())
    }
}
