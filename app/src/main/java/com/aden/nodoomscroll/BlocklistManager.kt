package com.aden.nodoomscroll

import android.view.accessibility.AccessibilityNodeInfo

/**
 * Stateless blocking rules (except the runtime `mode` toggle).
 *
 * SELECTIVE keys off resource-id (findAccessibilityNodeInfosByViewId) with an isVisibleToUser check,
 * NOT className/text: sections live in R8-obfuscated Views inside a single Activity whose ViewPager
 * retains offscreen fragments (so mere id-presence is unreliable — only the active screen is visible).
 * Requires android:accessibilityFlags="flagReportViewIds" in the config XML.
 */
object BlocklistManager {

    enum class Mode { BLOCK_WHOLE_APP, SELECTIVE }

    /** How "Retour" leaves a blocked screen. */
    enum class Escape { BACK, HOME }

    /** Toggled from the debug screen. */
    @Volatile
    var mode: Mode = Mode.SELECTIVE

    /**
     * @param blockedSections resource-id → human label. EMPTY = total-block app (e.g. TikTok).
     * @param escape BACK exits an in-app viewer (Reels tab OR a reel opened from a DM → back to the DM);
     *               HOME for total-block apps where BACK would just navigate within the blocked app.
     * @param pauseOnShow tap the media once before covering, so the video pauses instead of playing behind.
     */
    private data class Rule(
        val appName: String,
        val blockedSections: Map<String, String>,
        val escape: Escape = Escape.BACK,
        val pauseOnShow: Boolean = false,
        // If set, a blocked section only counts when this view is ALSO present. Used to require the
        // bottom nav bar for Instagram: the Reels FEED tab has it; a deliberately-opened reel viewer
        // (saved / DM / shared link) is a pushed full-screen viewer WITHOUT the nav bar → allowed.
        val guardViewId: String? = null,
    )

    private val rules: Map<String, Rule> = mapOf(
        // Spec: block ONLY Reels. Home feed, Explore/Search, DMs, Profile stay accessible (add people, message).
        // VERIFIED on Pixel 7A: clips_viewer_view_pager is vis=true only on the fullscreen reel viewer
        // (Reels tab and reels opened from DMs); on feed/explore/DMs it is absent or present-but-vis=false.
        "com.instagram.android" to Rule(
            appName = "Instagram",
            blockedSections = mapOf(
                "com.instagram.android:id/clips_viewer_view_pager" to "Reels",
            ),
            escape = Escape.BACK,
            pauseOnShow = true,
            // Only the Reels FEED tab (which shows the bottom nav bar) is blocked. Saved/DM/shared
            // reels open a pushed viewer with NO nav bar → guard absent → allowed.
            guardViewId = "com.instagram.android:id/feed_tab",
        ),
        "com.google.android.youtube" to Rule(
            appName = "YouTube",
            // PLACEHOLDER ids — harvest on-device before relying on YouTube Shorts blocking.
            blockedSections = mapOf(
                "com.google.android.youtube:id/reel_recycler" to "Shorts",
                "com.google.android.youtube:id/reel_player_page_container" to "Shorts",
            ),
            escape = Escape.BACK,
            pauseOnShow = true,
        ),
        "com.zhiliaoapp.musically" to Rule(
            appName = "TikTok",
            blockedSections = emptyMap(), // total block
            escape = Escape.HOME,
        ),
        "com.snapchat.android" to Rule(
            appName = "Snapchat",
            // PLACEHOLDER ids — harvest on-device.
            blockedSections = mapOf(
                "com.snapchat.android:id/discover_feed" to "Discover",
                "com.snapchat.android:id/spotlight_view" to "Spotlight",
            ),
            escape = Escape.BACK,
        ),
    )

    data class Verdict(
        val blocked: Boolean,
        val appName: String,
        val section: String,
        val escape: Escape = Escape.BACK,
        val pauseOnShow: Boolean = false,
        val packageName: String = "",
    )

    fun isKnownPackage(pkg: String?): Boolean = pkg != null && rules.containsKey(pkg)

    /**
     * @param root live active-window node (never a node captured earlier). May be null → in
     *             SELECTIVE we fail OPEN for this single event (never sticky) and let the trailing
     *             re-evaluation catch it once the tree is queryable.
     */
    /**
     * User-added custom apps (total-block). Populated by the service from Room; empty by default so
     * unit tests and the harvest path are unaffected. Keyed by package → display label.
     */
    @Volatile
    var customTotalBlock: Map<String, String> = emptyMap()

    fun evaluate(pkg: String?, root: AccessibilityNodeInfo?): Verdict {
        val rule = rules[pkg]
        if (rule == null) {
            // Not a native rule — is it a custom total-block app?
            val label = pkg?.let { customTotalBlock[it] }
            return if (label != null) Verdict(true, label, "app", Escape.HOME, false, pkg)
            else Verdict(false, "", "")
        }

        if (mode == Mode.BLOCK_WHOLE_APP) {
            return Verdict(true, rule.appName, "app", rule.escape, rule.pauseOnShow, pkg ?: "")
        }

        // SELECTIVE
        if (rule.blockedSections.isEmpty()) {
            return Verdict(true, rule.appName, "app", rule.escape, rule.pauseOnShow, pkg ?: "") // total-block app
        }
        if (root == null) {
            return Verdict(false, rule.appName, "") // fail-open, one event only
        }
        // Guard present? (e.g. the bottom nav bar). Absent → we're in a pushed, deliberately-opened
        // viewer (saved/DM/shared reel) → do not block.
        val guardPresent = rule.guardViewId == null ||
            !root.findAccessibilityNodeInfosByViewId(rule.guardViewId).isNullOrEmpty()

        for ((viewId, label) in rule.blockedSections) {
            val hits = root.findAccessibilityNodeInfosByViewId(viewId)
            // Require a VISIBLE hit: the tab ViewPager retains offscreen fragments in the node tree,
            // so mere presence is unreliable. isVisibleToUser marks the currently-active screen.
            if (hits != null && hits.any { it.isVisibleToUser } && guardPresent) {
                return Verdict(true, rule.appName, label, rule.escape, rule.pauseOnShow, pkg ?: "")
            }
        }
        return Verdict(false, rule.appName, "")
    }
}
