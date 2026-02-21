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
- Web: push to a `web/prod/deploy/**` branch — GitHub Actions builds the fat JAR, packages it into a Docker image, pushes to GHCR, and deploys to the VPS automatically
- The production URL (`https://api.dgkat.com`) is baked into the JS bundles at build time via `-PbackendBaseUrl`

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

**With prod URL** — `-PbackendBaseUrl` is **required** for `copyIslandsBundle` and `copySpaBundle`. The build will fail immediately if omitted. Set `BACKEND_BASE_URL` as a runtime env var for the SSR server (the server will also fail at startup in production if it is missing):
```shell
./gradlew :web:islands:copyIslandsBundle -PbackendBaseUrl=https://api.dgkat.com
./gradlew :web:webApp:copySpaBundle -PbackendBaseUrl=https://api.dgkat.com
BACKEND_BASE_URL=https://api.dgkat.com ./gradlew :web:ssr:run
```

The web app supports i18n with locale-based URL routing (`/en/...`, `/es/...`). Visiting `/` auto-detects the locale from the browser's `Accept-Language` header.

**Deploy to production** — push to any branch matching `web/prod/deploy/**`:
```shell
git push origin <your-branch>:web/prod/deploy/my-deploy
```
GitHub Actions will:
1. Build the fat JAR (with `https://api.dgkat.com` baked into the JS bundles)
2. Build a multi-platform Docker image (`linux/amd64` + `linux/arm64`) and push it to GHCR
3. SSH into the VPS, pull the new image, and restart the container

JS bundles include a content hash in their filename (e.g. `islands-bundle.a1b2c3d4.js`), so Cloudflare's cache is automatically busted on every deploy — no manual purge needed.

Required repository secrets:

| Secret | Description |
|---|---|
| `VPS_HOST` | VPS IP or hostname |
| `VPS_USER` | SSH user |
| `VPS_SSH_KEY` | Passphrase-less private SSH key |
| `GHCR_TOKEN` | GitHub PAT with `read:packages` scope (used by the VPS to pull the image) |

---

### Production Hardening

The following changes are in place to make production builds safe and performant. Some introduce new **required steps** when upgrading dependencies or building for release.

#### React build mode (automatic)

The SSR server automatically serves React development or production builds based on environment:

| How you run | React loaded |
|---|---|
| `./gradlew :web:ssr:run` | `react.development.js` (full warnings, larger) |
| Deployed JAR / Docker | `react.production.min.js` (optimised, ~3x smaller) |

No configuration needed — this is controlled by the `io.ktor.development` JVM flag that Ktor's Gradle plugin sets automatically.

#### CDN Subresource Integrity (SRI)

All external scripts (React, React DOM, HTMX, React Router DOM) are loaded with `integrity` and `crossorigin` attributes. The browser verifies the hash of each file before executing it. If the CDN is compromised or the file is tampered with, the script is blocked.

Hashes are stored in `web/ssr/src/main/kotlin/.../core/assets/ReactCdn.kt` alongside the pinned versions.

**When you upgrade React, HTMX, or React Router DOM**, you must regenerate the hashes:

```shell
# Re-hash current pinned versions (e.g. after confirming they haven't changed)
./gradlew :web:ssr:updateCdnHashes

# Upgrade to new versions and regenerate hashes in one command
./gradlew :web:ssr:updateCdnHashes \
  -PreactVersion=18.4.0 \
  -PhtmxVersion=2.0.0 \
  -PreactRouterVersion=7.0.0
```

This fetches the actual files from unpkg, computes SHA-384 hashes, and rewrites `ReactCdn.kt` automatically. Commit the updated file alongside your version bump.

**Current pinned versions:**

| Library | Version |
|---|---|
| React + React DOM | 18.3.1 |
| HTMX | 1.9.10 |
| React Router DOM | 6.30.3 |

#### Source maps not served in production

`.js.map` files are excluded from production builds. The `copyIslandsBundle` and `copySpaBundle` tasks copy only `.js` files to the SSR server. Any existing `.map` files are cleaned up on the next bundle copy.

#### CSS minification

CSS bundles are automatically minified during the `bundleCss` task (comments stripped, whitespace collapsed, redundant characters removed). This runs automatically as part of any SSR build. No manual step required.

#### Required environment variables

| Variable | Where required | What happens if missing |
|---|---|---|
| `-PbackendBaseUrl` | Gradle build (`copyIslandsBundle`, `copySpaBundle`) | Build fails immediately with a descriptive error |
| `BACKEND_BASE_URL` | SSR server runtime | Server fails to start in production (falls back to localhost in dev mode) |

---

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