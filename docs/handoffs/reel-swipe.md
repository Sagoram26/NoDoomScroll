# Handoff — NoDoomScroll — Reel-swipe blocking (in progress)

## Project
Android AccessibilityService, Kotlin, package `com.aden.nodoomscroll`, minSdk/target/compileSdk 36.
Device: Pixel 7A, ID `3A161JEHN09567`, USB debugging on, ADB at `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`.
Build: `JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`; `.\gradlew.bat installDebug`.

## Product state (all confirmed working on-device)
- Instagram **Reels tab** (bottom nav bar present) → blocked immediately, overlay shown, video auto-paused via `GLOBAL_ACTION_BACK`, `holdOverlay=true` until user taps "Retour".
- Instagram **home feed "Pour vous"** → NOT blocked (explicit user choice).
- Instagram **Explore** → NOT blocked (user wants to add people).
- **Saved reels** (Profil → ⋯ → Enregistré → filtre Reels → tap one) → NOT blocked. Verified: reel plays, no overlay, no auto-exit.
- **DM-reels** (reel received in a message) → NOT blocked, same code path as saved reels (user explicitly chose "laisser passer" for both, see AskUserQuestion answer below).
- Retour button confirmed working: clears overlay, returns to previous screen (Reels tab case).

## THE OPEN PROBLEM (why this handoff exists)
User's own words: *"le probleme c'est quand laissant passer les réel-dm et les réel enregistrer ta modification fait qu'on peut juste ouvrir l'un des deux et se mettre a scroller l'infini... aussi ducoup maitenant on peut ouvrir les réel de la page d'accueil et mettre a scroller depuis ça"*

Root cause: the current discriminator (`guardViewId = feed_tab`, i.e. "no bottom nav bar visible = allow") only distinguishes *which screen you're on*, not *what you do once there*. A saved reel, a DM reel, and a reel opened from the home feed all land in the same fullscreen `clips_viewer_view_pager` with no nav bar → currently all allowed **including infinite swiping to the next reel**, which is exactly the doomscroll behavior Phase 1 exists to stop.

**Desired fix:** allow watching the ONE reel the user deliberately opened (from saved/DM/home-feed) but block the moment they SWIPE to a different reel — show overlay + pause + auto-exit, same as the Reels-tab block.

Already asked and answered by user (do NOT re-ask): DM-reels should be **allowed** to open (same as saved reels) — the thing to block is the *swipe*, not the *entry point*. This applies uniformly to saved/DM/home-feed-opened reels.

## Current code state — BlocklistManager.kt
Instagram rule:
```kotlin
blockedSections = mapOf("com.instagram.android:id/clips_viewer_view_pager" to "Reels")
escape = Escape.BACK
pauseOnShow = true
guardViewId = "com.instagram.android:id/feed_tab"  // nav bar present = confirms "Reels tab" context
```
`evaluate()`: blocks if `hits.any { it.isVisibleToUser } && guardPresent`, where `guardPresent = guardViewId==null || findAccessibilityNodeInfosByViewId(guard).isNotEmpty()`.

This guard logic is what needs to change/extend — it currently has no notion of "which specific reel is on screen" or "did the user just swipe".

## Instrumentation already added (NOT YET USED — mid-experiment when session ended)
To get empirical truth before designing further, I added scroll-event instrumentation:

1. `app/src/main/res/xml/accessibility_service_config.xml` — added `typeViewScrolled` to `android:accessibilityEventTypes` (now `typeWindowStateChanged|typeWindowContentChanged|typeViewScrolled`).
2. `app/src/main/java/com/aden/nodoomscroll/NoDoomScrollService.kt`:
   - New const `INSTRUMENT_SCROLL = true` (companion object, alongside `HARVEST_ONLY`).
   - In `onAccessibilityEvent`, before the main `when`, added:
     ```kotlin
     if (INSTRUMENT_SCROLL && event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED &&
         pkg == "com.instagram.android") {
         logScroll(event)
     }
     ```
   - New method `logScroll(event)`: logs `src` (source viewIdResourceName), `from`/`to` index, `itemCount`, `scrollDeltaX/Y`, `scrollX/Y`, plus `clipsVis` (is `clips_viewer_view_pager` visible) and `navBar` (is `feed_tab` present) — to both `Log.i(IDS_TAG, msg)` and `EventLog.add(msg)`.

**Build status:** this instrumented version WAS built and installed successfully (`installDebug` succeeded). Service was toggled off/on via `adb shell settings put secure enabled_accessibility_services` to force it to re-read the new event-type subscription.

**Experiment that was interrupted:** navigated to Profil → Enregistré → Reels filter → tapped the first saved reel → was about to capture logcat for `SCROLL` lines during (a) open/settle with NO swipe (to check for spurious scroll events on open — this is a known risk per research) and (b) an actual upward swipe to reel #2 (to see if `TYPE_VIEW_SCROLLED` fires and what `from`/`to`/`src` values look like). The Bash call that would grep `adb logcat -d -s NoDoomScroll-IDS:I | grep SCROLL` failed due to a transient model/tooling error, not a real blocker. **Screen was left with `svc power stayon true`** (screen won't auto-lock) — remember to run `adb shell svc power stayon false` when done testing.

## Background research/design workflow (results available, use them)
A `Workflow` (id `wf_43a226aa-954` in this session, script name `reel-scroll-block-design`) was launched to research + design + adversarially-review two approaches:
- **Approach A**: scroll-event based (`TYPE_VIEW_SCROLLED` on the clips pager, with grace period after viewer opens).
- **Approach B**: reel-identity based (diff a stable per-reel identifier — e.g. author username node — on `TYPE_WINDOW_CONTENT_CHANGED`, no need to subscribe to scroll events at all).

It completed (see task notification `wixxg4hpr`, output file was under `...\tasks\wixxg4hpr.output` in the prior session's temp dir — may or may not still exist; if gone, the raw research text is preserved in this session's conversation history around the point the workflow completed). Key findings already extracted into context (do NOT re-run the research phase, it's expensive):

- ViewPager2 == internally a RecyclerView. Page-change is `TYPE_VIEW_SCROLLED`, not a distinct event type.
- On RecyclerView, `getFromIndex()`/`getToIndex()` ARE populated (via `LinearLayoutManager.onInitializeAccessibilityEvent`) = the visible adapter position(s). This is the reliable signal — track `lastReelIndex`, block when a scroll event's index differs from the last-seen baseline.
- `getScrollDeltaX/Y()` are NOT populated by RecyclerView (arrive as `UNDEFINED = -1`). Do not use them.
- `getScrollX/Y()` generally 0 for RecyclerView. Not reliable.
- Event source (`event.getSource()`) is likely the internal RecyclerView, not the `clips_viewer_view_pager` id itself — may need to check ancestry (walk up via `getParent()`) or just check `clipsVis` (as already logged) rather than gate strictly on `source.viewIdResourceName`.
- **Confirmed risk**: opening/settling a pager DOES tend to emit a spurious `TYPE_VIEW_SCROLLED` on open (settle animation). Must NOT block on the very first scroll event seen after entering the viewer — establish a baseline index on entry (during a short grace/settle window, e.g. 300-600ms, reusing the existing debounce infra) and only block when a *later* event's index differs from that baseline.
- Recommended design shape (from synthesis, not fully detailed here — re-run/re-read synthesis output if available, or proceed pragmatically): state machine per reel-viewer session: `ENTER_VIEWER (capture baseline index, ignore scrolls during grace period) → WATCHING (baseline locked) → on scroll with index != baseline → BLOCK (same pauseOnShow/autoExit/holdOverlay path already used for Reels tab)`. Reset state when `clips_viewer_view_pager` is no longer visible (user left the viewer entirely).

## Next steps for the new instance
1. Restore screen timeout if not already done: `adb shell svc power stayon false`.
2. Re-run the interrupted experiment: open a saved reel, capture logcat `SCROLL` lines for (a) open+idle (expect a spurious settle scroll — confirm baseline-skip is needed) and (b) an actual swipe up to reel #2 (confirm `from`/`to` index changes, confirm `clipsVis=true`, check what `src` looks like).
3. Based on real device data, implement the index-baseline state machine in `NoDoomScrollService.kt` (extend `dispatch()`/`evaluate()` — likely needs new per-service-instance state: `reelBaselineIndex: Int?`, `inGracePeriod`, entry/exit tracking keyed on `clips_viewer_view_pager` visibility transitions).
4. Remove/guard the temporary `INSTRUMENT_SCROLL` logging once the real detection lands (or leave `INSTRUMENT_SCROLL = false` as a debug toggle — user's call, low priority).
5. Rebuild, install, verify test matrix: Reels tab still blocks; home feed still free-scrolls; saved reel opens fine but swiping to reel #2 triggers block+pause+auto-exit+Retour-works; DM-reel same.
6. Still pending from earlier (lower priority, Phase 1 stretch): harvest real resource-ids for YouTube Shorts and Snapchat Discover/Spotlight (currently placeholder ids in `BlocklistManager.kt`); TikTok not installed, untested.

## Key gotchas already learned (don't rediscover)
- PowerShell `>` corrupts binary screenshots — use `adb shell screencap -p /sdcard/x.png` + `adb pull`, never `adb exec-out ... > file`.
- In this Bash tool (Git Bash), paths starting with `/sdcard/...` get mangled by MSYS path conversion unless `export MSYS_NO_PATHCONV=1` is set first.
- IG ignores accessibility-injected taps (`dispatchGesture`) — cannot programmatically dismiss/interact with IG UI that way; only `performGlobalAction` (BACK/HOME) reliably works.
- IG does not pause video on focus-loss or audio-focus-loss; only reliable stop is `GLOBAL_ACTION_BACK`.
- ViewPager/fragment retention means resource-id presence alone is unreliable across screens — must check `isVisibleToUser`.
- No `startForeground()` — this is a bound-only accessibility service; calling it throws `StartForegroundCalledOnStoppedServiceException`.
- Org/compliance note (must persist): the Anthropic subscription in use is for training/certification purposes only per org instructions — not for production/professional development. Mentioned here only for continuity; not a technical blocker on this task.
