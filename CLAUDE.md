# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

NoDoomScroll is a personal Android app that blocks addictive in-app content (Instagram Reels, YouTube Shorts, TikTok, Snapchat Discover/Spotlight) while leaving useful functions (DMs, stories, subscriptions) reachable. It works by running an `AccessibilityService` that watches foreground apps and drops a full-screen touch-consuming overlay when a blocked section is detected. All data is local; there is no backend.

**Current state: product UI implemented on top of the Phase 1 blocking engine.** `MainActivity` now hosts `ui/NdsApp` — an onboarding gate (`Settings.onboardingComplete`) then a bottom-nav shell (Home / Analytics / Apps / Settings). The original debug screen (`DebugScreen` in `MainActivity.kt`) is retained but no longer the entry point. Implemented from `docs/`: Room persistence (`data/`), streak + milestone logic (`StreakMath`, notified at 7/14/30/60/90/180/365), Mode Libre lifecycle (`ModeLibreController` + VM-scoped delay→session→reset), Glance 2×2 widget (`widget/`), weekly report + milestone notifications (`notif/`, WorkManager). Design system in `ui/theme/` (glass components, `NdsColors`, French `Format`).

**Still stubbed / Phase 3+:** grayscale is a persisted flag only (needs `WRITE_SECURE_SETTINGS`); Device Admin uninstall-protection and VPN not built; analytics heatmap + day-over-day deltas are placeholders; `secondsPerBlock = 120` in `NdsRepository` is the single time-saved calibration knob (docs give no constant). YouTube/Snapchat resource-ids in `BlocklistManager` are still placeholders.

### Domain layer (`data/`)

`NdsRepository` is the single domain entry point over the DAOs and owns every computation the PRD left unspecified (time-saved, block-rate, streak roll). `NdsDatabase` + repo are process singletons; the AccessibilityService and the Compose UI share one instance. The service writes (`logAttempt`, `refreshStreak` → returns a crossed milestone to notify); the UI reads via `AppViewModel` StateFlows. Custom (user-added) total-block apps are loaded into `BlocklistManager.customTotalBlock` on service connect — the accessibility config XML has **no `packageNames` filter** so custom apps deliver events (there's a `ponytail:` note on the battery tradeoff).

## Build / run / test

Requires JDK from Android Studio's bundled JBR. On Windows PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat installDebug        # build + install to connected device
.\gradlew.bat assembleDebug       # build APK only
.\gradlew.bat testDebugUnitTest   # JVM unit tests
.\gradlew.bat lint                # Android lint
```

Single unit test: `.\gradlew.bat testDebugUnitTest --tests "com.aden.nodoomscroll.ExampleUnitTest"`

Target device: Pixel 7A (Android 16 / API 36), `minSdk = targetSdk = compileSdk = 36`. ADB at `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`.

The AccessibilityService must be enabled by hand after install (Settings → Accessibility, or the "Réglages" button in the debug screen). After changing `accessibility_service_config.xml` (event types, package list), toggle the service off/on so it re-reads the subscription — a reinstall alone does not re-subscribe.

## Architecture

Event flow lives in four files, all in `app/src/main/java/com/aden/nodoomscroll/`:

- **`NoDoomScrollService.kt`** — the `AccessibilityService`. Receives events, decides when to scan, applies verdicts, manages the overlay lifecycle, audio focus, and reel-swipe state. This is where all timing/debounce/race handling lives.
- **`BlocklistManager.kt`** — stateless rules (one `Rule` per package) + `evaluate(pkg, root) → Verdict`. The single source of truth for *what* is blocked. Runtime `mode` toggle is its only mutable state.
- **`BlockingOverlay.kt`** — wraps one `TYPE_ACCESSIBILITY_OVERLAY` window. No `SYSTEM_ALERT_WINDOW` permission needed (added from the service context).
- **`EventLog.kt`** — in-process `StateFlow` ring buffer (cap 60). The service and `MainActivity` share a process, so the debug UI observes it directly. We do NOT read logcat (`READ_LOGS` is privileged); `Log.d` is emitted in parallel for `adb logcat`.

### Detection strategy (load-bearing, non-obvious)

Sections are identified by **resource-id + `isVisibleToUser`**, never by className or text. Reasons:
- App internals are R8-obfuscated, so class/text names are unstable across versions.
- A single Activity with a `ViewPager` retains offscreen fragments in the node tree, so mere id-*presence* is unreliable — only `isVisibleToUser` marks the currently-active screen. Requires `flagReportViewIds` in the config XML.

`Rule.guardViewId` adds a second condition: a section only counts as blocked when a guard view is also present. Instagram uses `feed_tab` (the bottom nav bar) as the guard so the **Reels tab** blocks, but a reel deliberately opened from saved/DM/a shared link — a pushed full-screen viewer with no nav bar — does not.

### Two modes

`BlocklistManager.Mode`: `SELECTIVE` (default — resource-id section blocking) vs `BLOCK_WHOLE_APP` (blocks any known package outright). A `Rule` with empty `blockedSections` (e.g. TikTok) is always a total block. Toggle via the debug screen's "Mode" button.

### Reel-swipe blocking (separate code path)

Blocking the *swipe* to the next reel (not the entry) runs through `TYPE_VIEW_SCROLLED` events, independent of the main `evaluate()` path. `handleReelScroll` records the first-seen `toIndex` as a baseline while `clips_viewer_view_pager` is visible; a later event with a different index means the user swiped → block (pause + auto-exit). See `docs/handoffs/reel-swipe.md` for the full design and device-verified behavior. The baseline is reset ONLY in `handleReelScroll` — do not add a reset to `dispatch()` (a `TYPE_WINDOW_STATE_CHANGED` race wiped it mid-swipe; there's a `ponytail:` comment marking this).

### Blocking mechanics

- No `startForeground()` — a bound-only AccessibilityService throws `StartForegroundCalledOnStoppedServiceException`. Foreground priority comes from the system binding.
- Reels/Shorts can't be paused in place (the app ignores injected taps). The service instead covers with the opaque overlay, grabs media audio focus, and fires `GLOBAL_ACTION_BACK` to truly stop playback, then holds the overlay (`holdOverlay`) until the user taps "Retour".
- Overlay add/remove is guarded by an `attached` flag AND try/catch: an uncaught throw inside `onAccessibilityEvent` kills the service process.
- Hide is debounced (`HIDE_DEBOUNCE_MS`) so a single transient not-blocked event (null root mid-scroll) doesn't flash the reel through.

### Debug flags

Compile-time consts in `NoDoomScrollService`'s companion object drive on-device investigation:
- `DUMP_IDS` / `HARVEST_ONLY` — BFS the live window and log resource-ids (to pick real discriminating ids). `HARVEST_ONLY` never blocks so screens stay navigable.
- `INSTRUMENT_SCROLL` — log every `TYPE_VIEW_SCROLLED` (source id, indices, deltas). Temporary; remove once reel-swipe detection is settled.

`docs/` (French) holds product/design specs, mockups, and engineering handoffs — start at `docs/index.md`. `docs/handoffs/global.md` is the PRD; `docs/handoffs/reel-swipe.md` is the current WIP feature.

## Platform gotchas (already learned — don't rediscover)

- Instagram ignores accessibility-injected taps (`dispatchGesture`); only `performGlobalAction` (BACK/HOME) works reliably.
- IG does not pause video on focus loss — only `GLOBAL_ACTION_BACK` stops it.
- YouTube and Snapchat resource-ids in `BlocklistManager` are **placeholders** — harvest real ids on-device before relying on them. TikTok is total-block (no ids needed) but untested (not installed).
- In the Bash tool (Git Bash), `/sdcard/...` paths get mangled by MSYS unless `export MSYS_NO_PATHCONV=1` first.
- PowerShell `>` corrupts binary screenshots — use `adb shell screencap -p /sdcard/x.png` + `adb pull`, never `adb exec-out ... > file`.
- Screen-timeout override for testing: `adb shell svc power stayon true` — remember to set it back to `false` when done.
