package com.aden.nodoomscroll

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView

/**
 * Manages a single TYPE_ACCESSIBILITY_OVERLAY window.
 *
 * Idempotency is an explicit MECHANISM, not an assertion: WindowManager.addView throws
 * IllegalStateException on an already-added view and removeView throws IllegalArgumentException
 * on a detached view. An uncaught throw inside onAccessibilityEvent kills the service process,
 * so both are guarded by an [attached] flag AND wrapped in try/catch.
 *
 * No SYSTEM_ALERT_WINDOW permission is needed: the overlay is added from the AccessibilityService
 * context. The view consumes touches (no FLAG_NOT_TOUCHABLE) so content underneath can't be
 * scrolled; FLAG_NOT_FOCUSABLE is fine (BACK routes to the app below but can't dismiss the overlay).
 */
class BlockingOverlay(
    private val context: Context,
    private val onRetour: () -> Unit,
) {
    private val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var view: View? = null
    private var attached = false

    private val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT,
    ).apply { gravity = Gravity.CENTER }

    /** Inflate once. Re-entrant safe: onServiceConnected can fire more than once. */
    fun preload() {
        if (view != null) return
        val v = LayoutInflater.from(context).inflate(R.layout.overlay_blocking, null)
        v.findViewById<View>(R.id.overlay_back).setOnClickListener { onRetour() }
        view = v
    }

    fun show(appName: String, section: String) {
        preload()
        val v = view ?: return
        v.findViewById<TextView>(R.id.overlay_section).text =
            if (section.isBlank() || section == "app") appName else "$appName · $section"
        if (attached) return
        try {
            windowManager.addView(v, params)
            attached = true
        } catch (e: IllegalStateException) {
            // Already added (racing add). Reconcile flag and move on.
            Log.w("NoDoomScroll", "overlay addView race: ${e.message}")
            attached = true
        }
    }

    fun hide() {
        val v = view ?: return
        if (!attached) return
        try {
            windowManager.removeView(v)
        } catch (e: IllegalArgumentException) {
            // Framework may have already detached it (e.g. service disconnect). Fine.
            Log.w("NoDoomScroll", "overlay removeView race: ${e.message}")
        } finally {
            attached = false
        }
    }
}
