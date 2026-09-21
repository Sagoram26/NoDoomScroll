package com.aden.nodoomscroll.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local-only schema (docs/handoffs/global.md §data model). No network, no cloud. Event tables
 * (attempts, sessions) are append-only; state tables (streak, config, settings) are single-row.
 */

/** One detected attempt to enter a blocked section. wasBlocked=false means it slipped through / was allowed. */
@Entity(tableName = "block_attempts")
data class BlockAttempt(
    @PrimaryKey val timestampMillis: Long,
    val packageName: String,
    val appLabel: String,
    val section: String,
    val wasBlocked: Boolean,
)

/** Single row (id=0). Streak = clean days since later of {activation, last mode-libre session}. */
@Entity(tableName = "streak_state")
data class StreakState(
    @PrimaryKey val id: Int = 0,
    val streakStartMillis: Long,
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val lastMilestoneNotified: Int,     // highest milestone (days) already notified; 0 = none
    val activationMillis: Long,          // first-ever activation (also analytics "données depuis")
)

/** Single row (id=0). Locked after onboarding; changing it requires reinstall in V1. */
@Entity(tableName = "mode_libre_config")
data class ModeLibreConfig(
    @PrimaryKey val id: Int = 0,
    val durationMin: Int,                // 5 / 10 / 15 / 30
    val frequency: String,               // DAILY / WEEKLY / NEVER
    val windowStartMin: Int,             // minutes-of-day, e.g. 12h -> 720; -1 = anytime
    val windowEndMin: Int,               // -1 = anytime
    val activationDelayMin: Int,         // 0 / 10 / 20 / 30 — "le temps que l'envie passe"
    val isLocked: Boolean,
) {
    companion object {
        const val DAILY = "DAILY"
        const val WEEKLY = "WEEKLY"
        const val NEVER = "NEVER"
    }
}

/** One consumed mode-libre session (resets the streak). */
@Entity(tableName = "mode_libre_sessions")
data class ModeLibreSession(
    @PrimaryKey val timestampMillis: Long,
    val durationMin: Int,
)

/** An app under NoDoomScroll's control. Native apps ship selective rules; custom apps are total/grayscale. */
@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,
    val appLabel: String,
    val blockType: String,               // SELECTIVE / TOTAL / GRAYSCALE_ONLY
    val blockedSectionsDesc: String,     // e.g. "Feed, Reels, Explore"
    val isNative: Boolean,               // one of the 4 built-ins
    val enabled: Boolean,
    val addedTimestamp: Long,
) {
    companion object {
        const val SELECTIVE = "SELECTIVE"
        const val TOTAL = "TOTAL"
        const val GRAYSCALE_ONLY = "GRAYSCALE_ONLY"
    }
}

/** Per-app screen-time baseline captured in onboarding écran 5 (slider 0–300 min/day). */
@Entity(tableName = "app_estimates")
data class AppEstimate(
    @PrimaryKey val packageName: String,
    val estimatedDailyMinutes: Int,
)

/** Single row (id=0). Simple flags/prefs — kept in Room so Compose can observe them as Flow. */
@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 0,
    val onboardingComplete: Boolean = false,
    val grayscaleEnabled: Boolean = false,
    val weeklyReportEnabled: Boolean = true,
    val weeklyReportDay: Int = 7,        // ISO day-of-week, 7 = Dimanche
    val weeklyReportHour: Int = 20,      // 20h
    val modeLibreEnabled: Boolean = true,
)
