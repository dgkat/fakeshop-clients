package org.example.fakeshop_clients

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


fun createMainScope(): CoroutineScope = MainScope()

fun cancelScope(scope: CoroutineScope) {
    scope.cancel()
}