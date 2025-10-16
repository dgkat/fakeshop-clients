plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ktor) apply false
}

tasks.register("runWebAppAndSSR") {
    group = "application"
    description = "Run both web app and server concurrently"

    doLast {
        val webAppProcess = ProcessBuilder()
            .command("./gradlew", ":webApp:jsBrowserDevelopmentRun", "--no-daemon")
            .inheritIO()
            .start()

        val serverProcess = ProcessBuilder()
            .command("./gradlew", ":ssr:run", "--no-daemon")
            .inheritIO()
            .start()

        // Create a shutdown hook to kill processes
        Runtime.getRuntime().addShutdownHook(Thread {
            println("Shutting down web app and server...")
            webAppProcess.descendants().forEach { it.destroy() }
            serverProcess.descendants().forEach { it.destroy() }
            webAppProcess.destroy()
            serverProcess.destroy()

            // Force kill if they don't stop gracefully
            Thread.sleep(2000)
            if (webAppProcess.isAlive) {
                webAppProcess.destroyForcibly()
            }
            if (serverProcess.isAlive) {
                serverProcess.destroyForcibly()
            }
        })

        // Register task completion listener
        gradle.buildFinished {
            webAppProcess.descendants().forEach { it.destroy() }
            serverProcess.descendants().forEach { it.destroy() }
            webAppProcess.destroy()
            serverProcess.destroy()
        }

        println("Web App starting on http://localhost:8080")
        println("Server starting on http://localhost:8081")
        println("Press Ctrl+C or stop the task to shutdown both services")

        // Wait for either process to complete (they shouldn't in dev mode)
        try {
            webAppProcess.waitFor()
            serverProcess.waitFor()
        } catch (e: InterruptedException) {
            println("Task interrupted, cleaning up...")
            webAppProcess.descendants().forEach { it.destroy() }
            serverProcess.descendants().forEach { it.destroy() }
            webAppProcess.destroy()
            serverProcess.destroy()
        }
    }
}