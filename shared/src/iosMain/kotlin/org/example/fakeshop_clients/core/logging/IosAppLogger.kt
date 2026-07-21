package org.example.fakeshop_clients.core.logging

class IosAppLogger : AppLogger {
    override fun debug(tag: String, message: String, throwable: Throwable?) = log("DEBUG", tag, message, throwable)
    override fun warn(tag: String, message: String, throwable: Throwable?) = log("WARN", tag, message, throwable)
    override fun error(tag: String, message: String, throwable: Throwable?) = log("ERROR", tag, message, throwable)

    private fun log(level: String, tag: String, message: String, throwable: Throwable?) {
        val suffix = throwable?.let { " | ${it::class.simpleName}: ${it.message}" } ?: ""
        println("$level/$tag: $message$suffix")
    }
}
