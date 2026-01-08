package org.example.fakeshop_clients.core.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Web (JavaScript) implementation of DispatcherProvider.
 * Note: JavaScript is single-threaded, so all dispatchers use the same event loop.
 * All dispatchers point to Dispatchers.Default which runs on the JS event loop.
 */
class WebDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.Default
    override val default: CoroutineDispatcher = Dispatchers.Default
}