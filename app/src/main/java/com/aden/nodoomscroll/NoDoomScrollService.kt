package com.aden.nodoomscroll

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.aden.nodoomscroll.data.ModeLibreController
import com.aden.nodoomscroll.data.NdsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Phase 1 prototype service. NO startForeground(): an *enabled* AccessibilityService is kept at
 * foreground priority by the system_server binding; startForeground() on this bound-only service
 * would throw StartForegroundCalledOnStoppedServiceException.
 */
class NoDoomScrollService : AccessibilityService() {

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val repo by lazy { NdsRepository.get(this) }

    private companion object {
        const val TAG = "NoDoomScroll"
        const val IDS_TAG = "NoDoomScroll-IDS"
        const val NODE_SCAN_INTERVAL_MS = 500L
        const val LAUNCH_DELAY_MS = 100L
        const val NAV_TIMEOUT_MS = 1500L
        const val HIDE_DEBOUNCE_MS = 600L // ride out transient not-blocked blips so the overlay stays stable
        const val AUTO_EXIT_DELAY_MS = 200L // cover with overlay, then exit the reel player
        // Harvest mode: log the resource-id set of each visible window for known packages, so real
        // discriminating ids can be picked (present on blocked screens, absent on DMs/profile).
        const val DUMP_IDS = false
        const val DUMP_INTERVAL_MS = 800L
        const val DUMP_MAX_NODES = 4000
        // While harvesting, dump ids but DO NOT show the overlay, so screens can be navigated freely.
        const val HARVEST_ONLY = false
        // Temporary: log every TYPE_VIEW_SCROLLED with source id / indices / delta to characterise
        // what a reel swipe emits vs what merely opening a reel emits. Remove once design is fixed.
        const val INSTRUMENT_SCROLL = true
    }

    private val audioManager by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val audioFocusRequest by lazy {
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build()
            )
            .build()
    }
    private var holdingAudioFocus = false

    private val handler = Handler(Looper.getMainLooper())
    private var overlay: BlockingOverlay? = null

    private var isNavigatingAway = false
    private var lastNodeScanAt = 0L
    private var lastDumpAt = 0L
    private var lastVerdictKey: String? = null

    // Escape resolved when the overlay is shown, used by the Retour tap.
    private var pendingEscape: BlocklistManager.Escape = BlocklistManager.Escape.BACK
    // True once we've auto-exited a reel (BACK) and are holding the overlay up until the user taps Retour.
    private var holdOverlay = false

    private var lastContentPackage: String? = null
    private val trailingScan = Runnable { scanAndDispatch(lastContentPackage, null) }
    private val clearNavigating = Runnable { isNavigatingAway = false }

    // Reel-swipe tracking (Instagram clips_viewer_view_pager): baseline = index of the ONE reel the
    // user deliberately opened (saved/DM/home-feed). A later scroll event with a different index =
    // user swiped to another reel -> block, same as the Reels-tab block. Reset when the viewer closes.
    private var reelBaselineIndex: Int? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        if (overlay == null) {
            overlay = BlockingOverlay(this, onRetour = ::onRetourTapped)
            overlay?.preload()
        }
        Log.d(TAG, "service connected")
        EventLog.add("service connected (mode=${BlocklistManager.mode})")
        // Load user-added custom apps (total-block) into the blocklist.
        ioScope.launch {
            val custom = repo.apps.enabled()
                .filter { !it.isNative && it.blockType == com.aden.nodoomscroll.data.BlockedApp.TOTAL }
                .associate { it.packageName to it.appLabel }
            BlocklistManager.customTotalBlock = custom
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val pkg = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: ""

        if (DUMP_IDS && BlocklistManager.isKnownPackage(pkg)) dumpIds(pkg)

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED && pkg == "com.instagram.android") {
            if (INSTRUMENT_SCROLL) logScroll(event)
            handleReelScroll(event)
        }

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                EventLog.add("STATE  $pkg  $className")
                // getSource() first (scoped, dodges the null/stale rootInActiveWindow race).
                val node = event.source ?: rootInActiveWindow
                dispatch(pkg, node)
            }

            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                // WHOLE_APP blocks on STATE_CHANGED alone; content events only matter for SELECTIVE
                // node reads. We still LOG them so the debug screen can harvest resource-ids.
                if (BlocklistManager.mode != BlocklistManager.Mode.SELECTIVE) return
                if (!BlocklistManager.isKnownPackage(pkg)) return

                // Cheap pre-filter: only scan on structural subtree changes, skip text/state churn.
                val types = event.contentChangeTypes
                if (types and AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE == 0) return

                lastContentPackage = pkg
                val now = SystemClock.uptimeMillis()
                val elapsed = now - lastNodeScanAt
                if (elapsed >= NODE_SCAN_INTERVAL_MS) {
                    lastNodeScanAt = now
                    scanAndDispatch(pkg, event.source)
                } else {
                    // Coalesce: (re)schedule a trailing scan that re-queries the LIVE tree at fire time.
                    handler.removeCallbacks(trailingScan)
                    handler.postDelayed(trailingScan, NODE_SCAN_INTERVAL_MS - elapsed)
                }
            }
        }
    }

    /** Trailing/coalesced scan path: always re-query the live active window, never a stale node. */
    private fun scanAndDispatch(pkg: String?, sourceHint: AccessibilityNodeInfo?) {
        pkg ?: return
        lastNodeScanAt = SystemClock.uptimeMillis()
        dispatch(pkg, sourceHint ?: rootInActiveWindow)
    }

    private fun dispatch(pkg: String, node: AccessibilityNodeInfo?) {
        if (HARVEST_ONLY) return // dump-only mode: never block, so screens stay navigable
        if (ModeLibreController.isUnlocked(System.currentTimeMillis())) {
            // mode libre active: treat every screen as not-blocked so the overlay comes down.
            applyVerdict(BlocklistManager.Verdict(blocked = false, appName = "", section = ""))
            return
        }
        if (holdOverlay) return  // frozen: overlay held after auto-exit until the user taps Retour
        // ponytail: reel-swipe baseline reset lives ONLY in handleReelScroll (TYPE_VIEW_SCROLLED).
        // A duplicate reset here, racing on TYPE_WINDOW_STATE_CHANGED's own rootInActiveWindow query,
        // wiped reelBaselineIndex mid-swipe (stale tree -> isVisibleToUser false for one event) and
        // silently disabled swipe blocking after the first reel. Don't re-add it.
        applyVerdict(BlocklistManager.evaluate(pkg, node))
    }

    private fun applyVerdict(verdict: BlocklistManager.Verdict) {
        if (verdict.blocked) {
            if (isNavigatingAway) return
            handler.removeCallbacks(hideRunnable) // cancel any pending debounced hide
            val key = "${verdict.appName}/${verdict.section}"
            pendingEscape = verdict.escape
            val firstDetection = key != lastVerdictKey
            if (firstDetection) {
                lastVerdictKey = key
                EventLog.add("BLOCK  ${verdict.appName}  ${verdict.section}")
                Log.d(TAG, "block $key")
                val pkgName = verdict.packageName
                val app = verdict.appName
                val section = verdict.section
                ioScope.launch {
                    repo.logAttempt(pkgName, app, section, wasBlocked = true)
                    repo.refreshStreak()?.let { milestone ->
                        com.aden.nodoomscroll.notif.Notifications.showMilestone(this@NoDoomScrollService, milestone)
                    }
                }
            }
            overlay?.show(verdict.appName, verdict.section)
            // pauseOnShow (reels/shorts): can't pause in place (IG ignores injected taps), so cover with
            // the opaque overlay, then auto-exit the player (BACK) to truly stop video+audio, and hold
            // the overlay up until the user taps Retour.
            if (verdict.pauseOnShow && firstDetection) {
                grabAudioFocus()
                handler.postDelayed(autoExitRunnable, AUTO_EXIT_DELAY_MS)
            }
        } else {
            if (isNavigatingAway) {
                isNavigatingAway = false
                handler.removeCallbacks(clearNavigating)
            }
            // Debounce the hide: a single transient not-blocked event (e.g. null root mid-scroll) must
            // NOT drop the overlay and let the reel flash through. Hide only after sustained not-blocked.
            handler.removeCallbacks(hideRunnable)
            handler.postDelayed(hideRunnable, HIDE_DEBOUNCE_MS)
        }
    }

    // Allow the ONE reel the user deliberately opened (saved/DM/home-feed); block the swipe to the
    // next one. Index-diff, not event-count: the settle scroll fired on open reports the SAME index
    // as the opened reel, so using the first-seen index as baseline is safe without a grace timer.
    private fun handleReelScroll(event: AccessibilityEvent) {
        if (HARVEST_ONLY || holdOverlay) return
        val clipsVisible = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(
            "com.instagram.android:id/clips_viewer_view_pager"
        )?.any { it.isVisibleToUser } ?: false
        if (!clipsVisible) return
        val toIndex = event.toIndex
        if (toIndex < 0) return
        val baseline = reelBaselineIndex
        if (baseline == null) {
            reelBaselineIndex = toIndex
            return
        }
        if (toIndex != baseline) {
            applyVerdict(BlocklistManager.Verdict(true, "Instagram", "Reels", BlocklistManager.Escape.BACK, pauseOnShow = true))
        }
    }

    /** Exit the reel/short viewer so playback truly stops; keep the overlay up (holdOverlay). */
    private val autoExitRunnable = Runnable {
        try {
            val ok = performGlobalAction(GLOBAL_ACTION_BACK)
            Log.d(TAG, "auto-exit BACK performed=$ok")
        } catch (e: Exception) {
            Log.w(TAG, "auto-exit BACK failed: ${e.message}")
        }
        holdOverlay = true
        EventLog.add("auto-exit reel (BACK), overlay held")
    }

    private val hideRunnable = Runnable {
        lastVerdictKey = null
        releaseAudioFocus()
        overlay?.hide()
    }

    /** Temporary instrumentation: characterise TYPE_VIEW_SCROLLED emitted inside Instagram. */
    private fun logScroll(event: AccessibilityEvent) {
        val src = event.source
        val srcId = src?.viewIdResourceName ?: "null"
        val root = rootInActiveWindow
        val clipsVis = root?.findAccessibilityNodeInfosByViewId(
            "com.instagram.android:id/clips_viewer_view_pager"
        )?.any { it.isVisibleToUser } ?: false
        val navBar = !root?.findAccessibilityNodeInfosByViewId(
            "com.instagram.android:id/feed_tab"
        ).isNullOrEmpty()
        val msg = "SCROLL src=$srcId from=${event.fromIndex} to=${event.toIndex} " +
            "cnt=${event.itemCount} dX=${event.scrollDeltaX} dY=${event.scrollDeltaY} " +
            "sX=${event.scrollX} sY=${event.scrollY} | clipsVis=$clipsVis navBar=$navBar"
        Log.i(IDS_TAG, msg)
        EventLog.add(msg)
    }

    /** Harvest: BFS the live window, log the distinct resource-ids for this package (throttled). */
    private fun dumpIds(pkg: String) {
        val now = SystemClock.uptimeMillis()
        if (now - lastDumpAt < DUMP_INTERVAL_MS) return
        lastDumpAt = now
        val root = rootInActiveWindow ?: return
        val prefix = "$pkg:id/"
        val ids = LinkedHashSet<String>()
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var visited = 0
        while (queue.isNotEmpty() && visited < DUMP_MAX_NODES) {
            val n = queue.removeFirst()
            visited++
            n.viewIdResourceName?.let { if (it.startsWith(prefix)) ids.add(it.removePrefix(prefix)) }
            for (i in 0 until n.childCount) n.getChild(i)?.let { queue.add(it) }
        }
        Log.i(IDS_TAG, "$pkg n=$visited ids[${ids.size}]=${ids.joinToString(",")}")

        if (pkg == "com.instagram.android") probeInstagram(root, pkg)
    }

    /** Probe: log tab BOUNDS (for scripted taps) + candidate content-marker VISIBILITY.
     *  isVisibleToUser distinguishes the active tab from retained-but-offscreen ViewPager fragments. */
    private fun probeInstagram(root: AccessibilityNodeInfo, pkg: String) {
        val tabs = listOf("feed_tab", "clips_tab", "search_tab", "direct_tab", "profile_tab")
        val tb = StringBuilder()
        for (t in tabs) {
            val n = root.findAccessibilityNodeInfosByViewId("$pkg:id/$t")?.firstOrNull()
            if (n != null) {
                val r = android.graphics.Rect(); n.getBoundsInScreen(r)
                tb.append("$t@${r.centerX()},${r.centerY()} ")
            }
        }
        Log.i(IDS_TAG, "TABBOUNDS $tb")

        val markers = listOf(
            "title_logo", "row_feed_photo_profile_imageview", "clips_viewer_view_pager",
            "clips_ufi_component", "grid_card_layout_container", "cf_hub_recycler_view",
            "row_inbox_container", "inbox_refreshable_thread_list_recyclerview",
            "profile_header_follow_button", "profile_viewpager",
        )
        val mb = StringBuilder()
        for (m in markers) {
            val hits = root.findAccessibilityNodeInfosByViewId("$pkg:id/$m")
            if (!hits.isNullOrEmpty()) {
                val vis = hits.any { it.isVisibleToUser }
                mb.append("$m{present=${hits.size},vis=$vis} ")
            }
        }
        Log.i(IDS_TAG, "MARKERS $mb")
    }

    /** Leave the blocked screen. If we already auto-exited (holdOverlay), just hide the overlay —
     *  we're already off the reel. Otherwise BACK exits the viewer (or a DM-opened reel → the DM),
     *  HOME for total-block apps. */
    private fun onRetourTapped() {
        if (holdOverlay) {
            // Already exited the reel automatically; the overlay was only being held. Just dismiss it.
            holdOverlay = false
            lastVerdictKey = null
            releaseAudioFocus()
            overlay?.hide()
            EventLog.add("RETOUR (was held)")
            return
        }

        isNavigatingAway = true
        handler.removeCallbacks(clearNavigating)
        handler.postDelayed(clearNavigating, NAV_TIMEOUT_MS)

        val action = when (pendingEscape) {
            BlocklistManager.Escape.HOME -> GLOBAL_ACTION_HOME
            BlocklistManager.Escape.BACK -> GLOBAL_ACTION_BACK
        }
        try {
            performGlobalAction(action)
        } catch (e: Exception) {
            Log.w(TAG, "escape ${pendingEscape} failed: ${e.message}")
        }
        releaseAudioFocus()
        handler.postDelayed({ overlay?.hide() }, LAUNCH_DELAY_MS)
        EventLog.add("RETOUR → $pendingEscape")
    }

    /** Take media audio focus → the reel/short loses focus and pauses (standard Android behaviour). */
    private fun grabAudioFocus() {
        if (holdingAudioFocus) return
        val res = audioManager.requestAudioFocus(audioFocusRequest)
        holdingAudioFocus = true
        Log.d(TAG, "grabAudioFocus res=$res")
    }

    private fun releaseAudioFocus() {
        if (!holdingAudioFocus) return
        audioManager.abandonAudioFocusRequest(audioFocusRequest)
        holdingAudioFocus = false
        Log.d(TAG, "releaseAudioFocus")
    }

    override fun onInterrupt() {}

    override fun onUnbind(intent: Intent?): Boolean {
        cleanup()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        cleanup()
        ioScope.cancel()
        super.onDestroy()
    }

    private fun cleanup() {
        handler.removeCallbacksAndMessages(null)
        holdOverlay = false
        releaseAudioFocus()
        overlay?.hide()
        EventLog.add("service destroyed")
    }
}
