package org.example.fakeshop_clients.core.logging

import android.util.Log

class AndroidAppLogger : AppLogger {
    override fun debug(tag: String, message: String, throwable: Throwable?) {
        Log.d(tag, message, throwable)
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}
