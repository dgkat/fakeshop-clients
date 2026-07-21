package org.example.fakeshop_clients.core.logging


interface AppLogger {
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun warn(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

object NoOpLogger : AppLogger {
    override fun debug(tag: String, message: String, throwable: Throwable?) {}
    override fun warn(tag: String, message: String, throwable: Throwable?) {}
    override fun error(tag: String, message: String, throwable: Throwable?) {}
}
