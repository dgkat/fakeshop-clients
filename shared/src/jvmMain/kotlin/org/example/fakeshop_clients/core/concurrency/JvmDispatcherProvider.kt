package org.example.fakeshop_clients.core.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * JVM implementation of DispatcherProvider for SSR server.
 * Provides access to JVM-specific coroutine dispatchers.
 */
class JvmDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}