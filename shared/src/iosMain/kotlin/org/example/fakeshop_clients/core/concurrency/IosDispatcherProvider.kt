package org.example.fakeshop_clients.core.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * iOS implementation of DispatcherProvider.
 * Note: iOS doesn't have a separate IO dispatcher, so both IO and Default use Dispatchers.Default.
 */
class IosDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.Default
    override val default: CoroutineDispatcher = Dispatchers.Default
}