This is a Kotlin Multiplatform project targeting Android, iOS, Web.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/web](./web) contains the web application modules:
  - [/web/ssr](./web/ssr) — Ktor server for server-side rendering (main entry point)
  - [/web/islands](./web/islands) — Interactive React components hydrated on SSR pages
  - [/web/webApp](./web/webApp) — Full SPA React application (favorites, profile, notifications)
  - [/web/common](./web/common) — Shared web resources (CSS, design tokens)
  - [/web/searchCommon](./web/searchCommon) — Shared search components used by both islands and SPA

### Environment Configuration

Each target automatically selects the correct backend URL based on build type — no manual changes needed between environments.

| Target | Dev URL | Prod URL | Mechanism |
|--------|---------|----------|-----------|
| Android | `http://10.0.2.2:8080` | `https://api.dgkat.com` | `BuildConfig.BASE_URL` per build type |
| iOS | `http://localhost:8080` | `https://api.dgkat.com` | `#if DEBUG` in `KoinHelper.swift` |
| Web (Islands + SPA) | `http://localhost:8080` | `https://api.dgkat.com` | `BACKEND_BASE_URL` env var at bundle build time |
| JVM / SSR | `http://localhost:8080` | `https://api.dgkat.com` | `BACKEND_BASE_URL` env var at runtime |

**Production deployment:**
- Web bundles: set `BACKEND_BASE_URL=https://api.dgkat.com` before running the Gradle bundle tasks
- SSR server: set `BACKEND_BASE_URL=https://api.dgkat.com` in the VPS environment (systemd unit, Docker env, etc.)

---

### Build and Run Android Application

By default the **debug** build points to `http://10.0.2.2:8080` (Android emulator localhost). The **release** build always uses `https://api.dgkat.com`.

**Run with dev URL (default):**
```shell
./gradlew :composeApp:assembleDebug
```

**Run with prod URL — one-off:** Temporarily change the debug URL in `shared/build.gradle.kts`:
```kotlin
debug { buildConfigField("String", "BASE_URL", "\"https://api.dgkat.com\"") }
```
Rebuild, test, then revert.

**Run with prod URL — persistent:** Add a `debugProd` build type to both `shared/build.gradle.kts` and `composeApp/build.gradle.kts` that inherits debug settings but overrides the URL:
```kotlin
// shared/build.gradle.kts  (inside android { buildTypes { ... } })
create("debugProd") {
    initWith(getByName("debug"))
    buildConfigField("String", "BASE_URL", "\"https://api.dgkat.com\"")
}
```
```kotlin
// composeApp/build.gradle.kts  (inside android { buildTypes { ... } })
create("debugProd") {
    initWith(getByName("debug"))
}
```
Then select the `debugProd` variant in your IDE's Build Variants panel and run normally.

---

### Build and Run iOS Application

By default a **Debug** scheme run points to `http://localhost:8080`. An **Archive / Release** build always uses `https://api.dgkat.com`.

**Run with dev URL (default):**

Use the run configuration from the IDE toolbar or open [/iosApp](./iosApp) in Xcode and run with the Debug scheme.

**Run with prod URL — one-off:** Temporarily edit the URL in `iosApp/iosApp/core/KoinHelper.swift`:
```swift
// Change:
let baseUrl = "http://localhost:8080"
// To:
let baseUrl = "https://api.dgkat.com"
```
Run, test, then revert.

**Run with prod URL — persistent (no code change):** Change the scheme's build configuration in Xcode:
1. Product → Scheme → Edit Scheme (or long-press the Run button)
2. Run → Info → Build Configuration → set to **Release**
3. Run on simulator as normal — `#if DEBUG` evaluates to false, prod URL is used

Switch back to **Debug** when done.

---

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
1. Install [Node.js](https://nodejs.org/en/download) (which includes `npm`)

2. Build and run the web application

**One command (recommended)** — cleans, generates strings, builds JS bundles, and starts the SSR server:
```shell
./gradlew :web:common:runWeb
```

**Step by step** — useful when you only need to rebuild specific parts:
- Islands bundle (will build and copy to ssr)
   ```shell
   ./gradlew :web:islands:copyIslandsBundle
   ```
- SPA (webApp) bundle (will build and copy to ssr)
   ```shell
   ./gradlew :web:webApp:copySpaBundle
   ```
- SSR server (serves both islands and SPA)
   ```shell
   ./gradlew :web:ssr:run
   ```

**With prod URL** — set `BACKEND_BASE_URL` before the bundle tasks (the URL is baked in at build time) and pass it to the SSR server at runtime:
```shell
BACKEND_BASE_URL=https://api.dgkat.com ./gradlew :web:islands:copyIslandsBundle
BACKEND_BASE_URL=https://api.dgkat.com ./gradlew :web:webApp:copySpaBundle
BACKEND_BASE_URL=https://api.dgkat.com ./gradlew :web:ssr:run
```

The web app supports i18n with locale-based URL routing (`/en/...`, `/es/...`). Visiting `/` auto-detects the locale from the browser's `Accept-Language` header.

**Troubleshooting**

For cache related issues:
   ```shell
  ./gradlew :clean
   ```
For yarn.lock related issues:
   ```shell
  ./gradlew :kotlinUpgradeYarnLock
   ```

### Shared Resource Generation

Design tokens (colors, typography, spacing) and strings are defined once in the `shared` module and generated into platform-native formats via Gradle tasks.

**Colors** — source of truth: [`DesignTokens.kt`](./shared/src/commonMain/kotlin/org/example/fakeshop_clients/core/design/DesignTokens.kt)

| Platform | Task | Output |
|----------|------|--------|
| Android | Direct Kotlin reference — no generation needed | `Color(Colors.Light.Primary)` |
| iOS | `./gradlew generateIosColors` | `iosApp/iosApp/Theme/FakeShopColors.swift` |
| Web | `./gradlew :web:common:generateThemeCss` | `web/common/.../css/shared/theme.css` |

**Strings** — source of truth: [`shared/.../resources/strings/en.json`](./shared/src/commonMain/resources/strings/en.json) and `es.json`

| Platform | Task | Output |
|----------|------|--------|
| Android + iOS (Compose) | `./gradlew generateComposeStrings` | `composeApp/.../composeResources/values/strings.xml` |
| iOS | `./gradlew generateIosStrings` | `iosApp/iosApp/<locale>.lproj/Localizable.strings` |
| Web (SSR + client) | `./gradlew generateWebStrings` | `web/common/.../resources/strings/` + `web/ssr/.../static/strings/` |
| Kotlin (shared) | `./gradlew generateStringKeys` | `shared/build/generated/strings/.../Strings.kt` |

Generate everything at once:
```shell
./gradlew generateComposeStrings generateIosStrings generateIosColors generateWebStrings generateStringKeys :web:common:generateThemeCss
```

See [`docs/shared-resources-guide.md`](./docs/shared-resources-guide.md) for full details.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…