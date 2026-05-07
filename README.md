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

### Release Android APK (Sideload)

1. **Update versions** in `composeApp/build.gradle.kts`:
   ```kotlin
   versionCode = 26MMDD01   // yymmdd + 2-digit sequence (e.g. 26050701)
   versionName = "x.y.z"
   ```

2. **Build the signed release APK** (requires keystore env vars):
   ```shell
   export FAKESHOP_KEYSTORE=/Users/dimitrioskatoudis/.keystores/fakeshop-release.jks
   export FAKESHOP_KEYSTORE_PASSWORD=yourKeystorePassword
   export FAKESHOP_KEY_ALIAS=yourKeyAlias
   export FAKESHOP_KEY_PASSWORD=yourKeyPassword

   ./gradlew :composeApp:assembleProdRelease
   ```
   Output: `composeApp/prod/release/composeApp-prod-release.apk`

3. **Rename the APK**:
   ```shell
   cp composeApp/prod/release/composeApp-prod-release.apk fakeshop.apk
   ```

4. **Create a GitHub Release** at [https://github.com/dgkat/fakeshop-android-releases/releases/new](https://github.com/dgkat/fakeshop-android-releases/releases/new):
   - Create a new tag (e.g. `v1.0.1`)
   - Add a title and release notes
   - Attach `fakeshop.apk`
   - Publish

The sideload URL `https://github.com/dgkat/fakeshop-android-releases/releases/latest/download/fakeshop.apk` always points to the latest release automatically.

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

### Favorites & Recently Viewed

Users can heart products to save them as favorites and the backend records every product detail view as a "recently seen" entry (max 50, 7-day TTL). Both lists live behind a single **Favorites** tab in the app (Favorites / Recently Seen segmented tabs).

#### Backend endpoints

All routes require authentication. Web uses `/api/web/...`, mobile uses `/api/mobile/...`.

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/favorites/{productId}` | Add to favorites (201, empty body) |
| `DELETE` | `/favorites/{productId}` | Remove from favorites (204, empty body) |
| `GET` | `/favorites` | Enriched `BriefProduct` list |
| `GET` | `/favorites/check?productId={id}` | `{ "isFavorited": bool }` |
| `POST` | `/favorites/check-bulk` | `{ favoritedProductIds: [...] }` from a list of ids |
| `GET` | `/recents` | Enriched `BriefProduct` list, most-recent first |

Recents are recorded automatically by the gateway on `GET /products/{id}` — no client write needed.

#### Screens that use favorites

| Screen | Interaction |
|---|---|
| **Home / product list** (Android, iOS, Web islands, SPA home) | Calls `checkBulkFavorites` after products load, renders filled hearts on each card, `toggleFavorite` on tap |
| **Product detail** (all platforms) | Calls `checkFavorite(id)` on load, toggles via heart button, state reflected immediately across other screens |
| **Favorites tab** (`/favorites` on web, Favorites tab on mobile) | Calls `getFavorites()` on mount, supports swipe-to-remove / heart-tap to remove |
| **Recently Seen tab** (same screen, second tab) | Lazy-loads via `getRecentlyViewed()` the first time the user switches to the tab |

#### Shared module architecture

```
shared/…/features/favorites/
├── data/
│   ├── FavoritesCache.kt              ← interface — in-memory StateFlow<Set<String>>
│   ├── FavoritesDatasource(Impl).kt   ← Ktor/Axios HTTP calls
│   ├── FavoritesRepositoryImpl.kt     ← maps DTOs → domain, updates cache on every response
│   ├── models/                        ← BulkFavoriteCheck{Request,Response}, FavoriteCheckResponse
│   └── MobileFavoritesCache.kt        ← mobileMain impl (plain MutableStateFlow)
├── domain/
│   ├── FavoritesRepository.kt
│   ├── FavoritesService(Impl).kt      ← exposes favoritedIds: StateFlow<Set<String>>
└── presentation/
    ├── FavoritesError.kt              ← sealed interface (Network only)
    ├── FavoritesState / Event
    └── FavoritesViewStore.kt          ← collects service.favoritedIds, filters on removal

shared/…/features/recents/             ← read-only mirror of favorites, no cache, just fetch + state
shared/src/jsMain/…/features/favorites/data/WebFavoritesCache.kt   ← sessionStorage-backed cache
```

**The `FavoritesCache` singleton is the source of truth for heart state.** Every ViewStore that cares about favorites (`ProductListViewStore`, `ProductDetailViewStore`, `FavoritesViewStore`) observes `favoritesService.favoritedIds` and reacts to changes — so liking a product anywhere instantly updates every other screen without re-fetching. The repository writes the cache after every API response (`getFavorites`, `checkFavorite`, `checkBulkFavorites`) and toggles it optimistically on `toggleFavorite`, reverting if the HTTP call fails.

`ProfileViewStore.handleLogout()` calls `favoritesService.clearCache()` after a successful logout so the next user starts clean.

#### Per-platform state wiring

| Platform | Cache impl | How the UI observes state |
|---|---|---|
| **Android** | `MobileFavoritesCache` (singleton via `mobileFavoritesCacheModule`) | `FavoritesViewModel` wraps `FavoritesViewStore` + `RecentsViewStore` with `viewModelScope`, exposes their `StateFlow`s directly; Compose uses `collectAsStateWithLifecycle()` |
| **iOS** | `MobileFavoritesCache` (same mobileMain singleton) | `FavoritesViewModel: ObservableObject` creates a `CoroutineScope` via `ScopeHelper`, resolves the ViewStores from Koin, bridges their `StateFlow` to `@Published` via `for try await` tasks in `init`, cancels the scope in `deinit` |
| **Web islands** (home page) | `WebFavoritesCache` (sessionStorage-backed) registered in `WebKoinManager` via `webFavoritesCacheModule` | Islands `ProductListViewmodel` delegates to `ProductListViewStore`; `ProductCard` reads `isFavorited = product.id in state.favoritedProductIds` and calls `vm.toggleFavorite(id)` |
| **Web SPA** (`/favorites`) | `WebFavoritesCache` (same jsMain singleton, but a **new Koin context** per page load) | `FavoritesPage` FC resolves `FavoritesViewModel` from Koin, uses `useEffectWithCleanup` to collect both state flows via `launchIn(MainScope())`, cancels jobs on unmount |

**Why web needs sessionStorage** — islands (home) and SPA (`/favorites`) are separate webpack bundles with separate Koin contexts. A full-page navigation from `/` to `/favorites` tears down one Koin graph and starts another. `WebFavoritesCache` persists the id set to `sessionStorage["favorited_ids"]` on every mutation and reads it back in `init`, so the SPA sees the user's up-to-date favorite set instantly (a fresh `checkBulkFavorites` / `getFavorites` call still runs in the background to reconcile with the backend).

#### DI graph

- `favoritesModule` (commonMain) — datasource, repository, service. `FavoritesService` is a `single` so the observable `StateFlow` is shared across all consumers.
- `mobileFavoritesCacheModule` (mobileMain) — binds `FavoritesCache` to `MobileFavoritesCache`, included from `mobileInfrastructureModule`.
- `webFavoritesCacheModule` (jsMain) — binds `FavoritesCache` to `WebFavoritesCache`, wired into both `WebKoinManager` (islands) and `WebCoreModule` (SPA).
- `recentsModule` (commonMain) — datasource + repository + service. No cache: recents are a pure read model.
- Platform UI modules (`androidFavoritesModule`, `webFavoritesModule`, iOS's `iosModule`) provide the ViewStore / ViewModel factories and inject `CoroutineScope` appropriately.

See `FEATURE_INTERACTIONS_CLIENT.md` for the original implementation plan and `FAVORITES_STATE_SYNC_PLAN.md` for the cache-based cross-screen sync design.

---

### Push Notifications

Price-drop push notifications are wired up on Android and Web. iOS is **not yet implemented** — the shared module is ready, but the `iosApp` target still needs Firebase SDK integration and an `AppDelegate` (see `FEATURE_NOTIFICATIONS_CLIENT.md` Step 8).

#### Architecture

| Platform | Delivery | SDK | Token source |
|---|---|---|---|
| Android | FCM (native) | `firebase-messaging` via `google-services` plugin | `FirebaseMessaging.getInstance().token` |
| iOS | FCM → APNs (pending) | `firebase-ios-sdk` (pending) | `Messaging.messaging().token` (pending) |
| Web | Firebase JS SDK (compat) + Service Worker + VAPID | Lazy-loaded from `gstatic.com` on first use | `firebase.messaging().getToken({ vapidKey, serviceWorkerRegistration })` |

**Shared module** (`shared/.../features/notifications`) owns the datasource, repository, service, `PushTokenProvider` + `NotificationPermissionManager` interfaces, and the `NotificationPrefsViewStore`. Each platform provides its own implementations of the two interfaces and wires them into Koin.

**Token lifecycle** — token is obtained once permission is granted, cached locally (Android uses `AndroidPendingDeviceTokenCache` to hold the token until the user is logged in), then POSTed to `/api/{mobile|web}/device-tokens`. On logout, `ProfileViewStore.handleLogout()` DELETEs the current token. Token refresh (Android `onNewToken`, web re-subscribe) re-registers automatically.

**Foreground delivery** — messages arriving while the app is open are surfaced via `NotificationEventBus` (emits `PushNotificationEvent.PriceDrop`). Android shows a snackbar with a "View" action; web shows an OS notification (the service worker always displays, regardless of foreground state).

**Deep-link on tap** — the push payload carries `data.productId`. Tapping navigates to the product detail screen on each platform (Android: `MainActivity` intent extra → `navController.navigate`; web: service worker `notificationclick` → `clients.openWindow('/{locale}/product/{productId}')`).

#### Firebase projects: dev vs prod

**Use two separate Firebase projects** — one for local development, one for production. This keeps test pushes out of real users' devices and lets you rotate keys independently.

| Value | Dev source | Prod source |
|---|---|---|
| Android `google-services.json` | `composeApp/google-services.json` (gitignored, hand-placed) | Same path, but pulled from a CI secret at build time |
| Web Firebase config (`apiKey`, `appId`, …) | Defaults hardcoded in `web/ssr/src/main/resources/application.conf` | `FIREBASE_*` env vars on the SSR container (override the conf defaults) |
| Web VAPID public key | `firebase.vapidKey` default in `application.conf` | `FIREBASE_VAPID_KEY` env var |

The Firebase web config values (`apiKey`, `projectId`, etc.) **are public** — they ship to every browser. The real security boundary is API key restrictions in the Google Cloud Console (restrict to your prod domain) and Firebase Security Rules. Injecting via env vars is about project separation, not secrecy.

#### Dev setup

**Android** — download `google-services.json` from your dev Firebase project (Project settings → Your apps → Android app) and drop it at `composeApp/google-services.json`. The file is already in `.gitignore`. Build + run as normal:

```shell
./gradlew :composeApp:assembleDebug
```

**Web** — nothing to do. `application.conf` ships with dev Firebase config baked in, and the VAPID key is preconfigured. Run the SSR server normally:

```shell
./gradlew :web:common:runWeb
```

Open `/favorites`, accept the notification permission when prompted (or from the Profile page), and send a test push via Firebase Console → Cloud Messaging → "Send test message" (using the token printed in the backend logs after registration).

#### Prod setup

**Android prod build** — replace `composeApp/google-services.json` with the one from your prod Firebase project before running `assembleRelease`. In CI this is done by decoding a base64-encoded secret into the file path just before the Gradle build.

**Web prod build** — the SSR container reads Firebase config from env vars. Set these on the container (via `docker-compose.yml` `environment:` or an `.env` file on the VPS):

```shell
FIREBASE_API_KEY=...
FIREBASE_AUTH_DOMAIN=<prod-project>.firebaseapp.com
FIREBASE_PROJECT_ID=<prod-project>
FIREBASE_STORAGE_BUCKET=<prod-project>.firebasestorage.app
FIREBASE_MESSAGING_SENDER_ID=...
FIREBASE_APP_ID=...
FIREBASE_VAPID_KEY=...
```

Each overrides the corresponding default in `application.conf`. Missing values fall back to the dev defaults — if you forget to set one, you'll ship dev Firebase config to prod users. **Double-check all seven are set before the first prod deploy.**

The service worker (`/firebase-messaging-sw.js`) is served **dynamically** by the Ktor SSR server (not as a static file) so the Firebase config can be injected at request time — the same config already used by `SpaPage.kt` for the HTML. The worker imports the Firebase Messaging compat SDK via `importScripts` and uses `messaging.onBackgroundMessage()` to handle incoming FCM pushes. This is required for Chrome on Android, where FCM routes pushes through Google Play Services rather than the standard Web Push protocol; the raw `push` event approach that works on desktop Chrome is not triggered on Android Chrome without the Firebase SDK in the service worker. `onBackgroundMessage()` works across all platforms (desktop Chrome, Android Chrome, and iOS Safari 16.4+).

#### Wiring prod secrets

**Web — `.env` file on the VPS**

The seven `FIREBASE_*` values live in an `.env` file on the VPS, next to `docker-compose.yml`. They never touch GitHub Actions, never appear in CI logs, and never traverse SSH. Rotating a key is a one-line edit on the box plus a `docker compose up -d`.

**First-time setup** — one-shot, performed manually on the VPS:

1. Grab the values from Firebase Console → Project settings → Your apps → Web app (for `apiKey`, `authDomain`, `projectId`, `storageBucket`, `messagingSenderId`, `appId`) and Project settings → Cloud Messaging → Web Push certificates (for `vapidKey`). Use the **prod** Firebase project, not dev.

2. SSH into the VPS and open the compose directory:
   ```shell
   ssh <user>@<vps-host>
   cd /opt/fakeshop/prod_env
   ```

3. Create `.env` next to `docker-compose.yml` with the seven values:
   ```shell
   cat > .env <<'EOF'
   FIREBASE_API_KEY=AIza...
   FIREBASE_AUTH_DOMAIN=fakeshop-prod.firebaseapp.com
   FIREBASE_PROJECT_ID=fakeshop-prod
   FIREBASE_STORAGE_BUCKET=fakeshop-prod.firebasestorage.app
   FIREBASE_MESSAGING_SENDER_ID=123456789012
   FIREBASE_APP_ID=1:123456789012:web:abcdef1234567890
   FIREBASE_VAPID_KEY=B...
   EOF
   ```

4. Lock it down so only the deploy user can read it:
   ```shell
   chmod 600 .env
   ```

5. Edit `docker-compose.yml` and add an `env_file:` entry under the `fakeshop-web` service (Compose reads the file at `up` time and injects every line as an env var into the container):
   ```yaml
   services:
     fakeshop-web:
       image: ghcr.io/<owner>/fakeshop-web:latest
       env_file:
         - .env
       # ...existing config (ports, restart policy, BACKEND_BASE_URL, etc.)
   ```
   If `BACKEND_BASE_URL` is currently set inline under `environment:`, leave it there — `env_file:` and `environment:` merge, with `environment:` taking precedence.

6. Restart the container to pick up the new env:
   ```shell
   docker compose up -d fakeshop-web
   ```

7. Verify the prod project is actually being served: open `https://<your-domain>/en/favorites` in a browser, open DevTools → Console, and run:
   ```js
   window.__FIREBASE_CONFIG__
   ```
   The `projectId` should be your prod project. If it's still `fakeshop-dev`, the env vars didn't reach the container — check `docker compose config` on the VPS to see the merged config and confirm `.env` is being read.

**Rotating a key later** — SSH in, edit `.env`, `docker compose up -d fakeshop-web`. Nothing else to do. The next deploy from `web/prod/deploy/**` picks up the new values automatically because the env file is read on every `up`.

**Backup** — this `.env` is the only place these values exist outside Firebase Console. If you rebuild the VPS, you'll need to recreate it from scratch or restore it from a secure backup (e.g. 1Password, encrypted tarball). Add it to whatever disaster-recovery checklist you keep for the VPS.

**Android — GitHub Actions secret (when a release workflow is added)**

Android's `google-services.json` is a file, not env vars, so the VPS-`.env` pattern doesn't apply. Store it as a base64-encoded GitHub secret and decode it before the Gradle build:

| Secret | Notes |
|---|---|
| `GOOGLE_SERVICES_JSON_PROD` | Base64-encoded `google-services.json` from the prod Firebase project |

Encode locally:
```shell
base64 -i composeApp/google-services.json | pbcopy
```

Decode in the release workflow:
```yaml
- name: Restore google-services.json
  run: echo "${{ secrets.GOOGLE_SERVICES_JSON_PROD }}" | base64 -d > composeApp/google-services.json
- run: ./gradlew :composeApp:assembleRelease
```

#### iOS — remaining work

The shared module already exposes everything iOS needs (`NotificationsService`, `NotificationPrefsViewStore`, `NotificationEventBus`). To finish the iOS side:

1. Add `firebase-ios-sdk` via SPM (FirebaseMessaging product) in Xcode
2. Add `GoogleService-Info.plist` (dev + prod variants managed per scheme)
3. Enable **Push Notifications** capability and **Background Modes → Remote notifications**
4. Create `AppDelegate.swift` with `FirebaseApp.configure()` + `MessagingDelegate` + `UNUserNotificationCenterDelegate`
5. Implement `IOSPushTokenProvider` and `IOSNotificationPermissionManager` in `iosMain`
6. Wire them into `iosModule.kt` and expose via `IOSKoinHelper`
7. Add a `NotificationRouter` `ObservableObject` and observe it from `MainTabView` for deep-linking

Full breakdown in `FEATURE_NOTIFICATIONS_CLIENT.md` → Step 8.

---

### Production Hardening

The following changes are in place to make production builds safe and performant. Some introduce new **required steps** when upgrading dependencies or building for release.

#### React bundled by webpack (not CDN)

React 19 removed UMD builds, so React and React DOM cannot be loaded from a CDN. They are bundled directly into the islands and SPA webpack bundles. No CDN scripts, no global `window.React` — webpack handles everything.

The Kotlin React wrappers (`2025.10.11-19.2.0`) target React 19, which is pulled from npm at build time and included in the output bundle.

#### CDN Subresource Integrity (SRI) — HTMX only

HTMX is the only external script loaded from a CDN. It is loaded with `integrity` and `crossorigin="anonymous"` attributes so the browser verifies the file's SHA-384 hash before executing it. If the CDN is compromised or the file is tampered with, the script is blocked.

The hash and pinned version are stored in `web/ssr/src/main/kotlin/.../core/assets/ExternalScripts.kt`.

**When you upgrade HTMX**, regenerate the hash:

```shell
# Re-hash the current pinned version
./gradlew :web:ssr:updateCdnHashes

# Upgrade to a new version and regenerate the hash in one command
./gradlew :web:ssr:updateCdnHashes -PhtmxVersion=2.0.0
```

This fetches the file from unpkg, computes the SHA-384 hash, and rewrites `ExternalScripts.kt` automatically. Commit the updated file alongside your version bump.

**Current pinned CDN versions:**

| Library | Version |
|---|---|
| HTMX | 1.9.10 |

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