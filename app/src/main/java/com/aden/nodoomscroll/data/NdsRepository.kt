package com.aden.nodoomscroll.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Single domain entry point over the DAOs. Holds the calculations the PRD left unspecified
 * (time-saved, block-rate, streak roll) so there is exactly one source of truth — see the
 * `ponytail:` notes where a constant is a product decision, not a fact from the docs.
 */
class NdsRepository private constructor(private val db: NdsDatabase) {

    val attempts = db.blockAttemptDao()
    val streak = db.streakDao()
    val modeLibre = db.modeLibreDao()
    val apps = db.blockedAppDao()
    val estimates = db.appEstimateDao()
    private val settingsDao = db.settingsDao()

    // ponytail: docs give no seconds-per-block constant (all analytics numbers are examples). One
    // averted feed/reels entry ~= 2 min of avoided scrolling. This is THE calibration knob for every
    // "temps gagné" figure — tune it, don't scatter it.
    val secondsPerBlock = 120L

    val milestones = StreakMath.MILESTONES

    fun observeSettings(): Flow<Settings?> = settingsDao.observe()
    fun observeStreak(): Flow<StreakState?> = streak.observe()
    fun observeApps(): Flow<List<BlockedApp>> = apps.observeAll()
    fun observeModeLibreConfig(): Flow<ModeLibreConfig?> = modeLibre.observeConfig()

    fun blockedTodayFlow(): Flow<Int> = attempts.blockedCountSince(startOfToday())
    fun attemptsTodayFlow(): Flow<List<BlockAttempt>> = attempts.attemptsSince(startOfToday())
    fun recentBypassesFlow(limit: Int = 10): Flow<List<BlockAttempt>> = attempts.recentBypasses(limit)

    suspend fun getSettings(): Settings = settingsDao.get() ?: Settings().also { settingsDao.upsert(it) }
    suspend fun updateSettings(transform: (Settings) -> Settings) {
        settingsDao.upsert(transform(getSettings()))
    }

    /** Called by the accessibility service each time a blocked section is detected. */
    suspend fun logAttempt(pkg: String, appLabel: String, section: String, wasBlocked: Boolean) {
        attempts.insert(BlockAttempt(nowMillis(), pkg, appLabel, section, wasBlocked))
    }

    // --- Streak ---------------------------------------------------------------

    /** Whole clean days between [fromMillis] and [toMillis], counted by local calendar date. */
    fun cleanDaysBetween(fromMillis: Long, toMillis: Long): Int {
        if (toMillis <= fromMillis) return 0
        return TimeUnit.MILLISECONDS.toDays(startOfDay(toMillis) - startOfDay(fromMillis)).toInt()
    }

    /**
     * Recompute the streak from persisted anchors. Streak base = later of {activation, last mode
     * libre session}; value = clean local days since. Returns the newly crossed milestone (days) if
     * this recompute pushed the streak past one not yet notified, else null.
     */
    suspend fun refreshStreak(): Int? {
        val existing = streak.get() ?: run {
            // First run without an explicit activation: anchor now.
            val now = nowMillis()
            streak.upsert(StreakState(0, now, 0, 0, 0, now))
            return null
        }
        val lastSession = modeLibre.lastSessionMillis() ?: 0L
        val base = maxOf(existing.streakStartMillis, lastSession, existing.activationMillis)
        val days = cleanDaysBetween(base, nowMillis())
        val best = maxOf(existing.bestStreakDays, days)

        val (crossed, notified) = StreakMath.evaluate(days, existing.lastMilestoneNotified)

        streak.upsert(existing.copy(
            streakStartMillis = base,
            currentStreakDays = days,
            bestStreakDays = best,
            lastMilestoneNotified = notified,
        ))
        return crossed
    }

    /** Record activation once (onboarding complete). Sets both anchors to now. */
    suspend fun activate() {
        val now = nowMillis()
        if (streak.get() == null) streak.upsert(StreakState(0, now, 0, 0, 0, now))
    }

    /** Seed the 4 built-in native apps (matches BlocklistManager rules) if not already present. */
    suspend fun seedNativeApps() {
        if (apps.count() > 0) return
        val now = nowMillis()
        listOf(
            BlockedApp("com.instagram.android", "Instagram", BlockedApp.SELECTIVE, "Feed, Reels, Explore", true, true, now),
            BlockedApp("com.google.android.youtube", "YouTube", BlockedApp.SELECTIVE, "Shorts, Accueil", true, true, now),
            BlockedApp("com.zhiliaoapp.musically", "TikTok", BlockedApp.TOTAL, "Tout", true, true, now),
            BlockedApp("com.snapchat.android", "Snapchat", BlockedApp.SELECTIVE, "Discover, Spotlight", true, true, now),
        ).forEach { apps.upsert(it) }
    }

    /** Consume a mode-libre session → resets the streak to 0 from now. */
    suspend fun useModeLibreSession() {
        val now = nowMillis()
        modeLibre.insertSession(ModeLibreSession(now, modeLibre.getConfig()?.durationMin ?: 0))
        val s = streak.get()
        if (s != null) {
            streak.upsert(s.copy(streakStartMillis = now, currentStreakDays = 0, lastMilestoneNotified = 0))
        }
    }

    // --- Time saved -----------------------------------------------------------

    /** Seconds saved from a blocked-attempt count. */
    fun secondsSaved(blockedCount: Int): Long = blockedCount * secondsPerBlock

    fun secondsSavedTodayFlow(): Flow<Int> = attempts.blockedCountSince(startOfToday())

    // --- Mode libre availability ---------------------------------------------

    sealed interface ModeLibreStatus {
        object Available : ModeLibreStatus
        data class OutOfWindow(val startMin: Int) : ModeLibreStatus
        data class QuotaExhausted(val nextMillis: Long) : ModeLibreStatus
        object NeverConfigured : ModeLibreStatus
    }

    suspend fun modeLibreStatus(): ModeLibreStatus {
        val cfg = modeLibre.getConfig() ?: return ModeLibreStatus.NeverConfigured
        if (cfg.frequency == ModeLibreConfig.NEVER) return ModeLibreStatus.NeverConfigured

        // Time window
        if (cfg.windowStartMin >= 0 && cfg.windowEndMin >= 0) {
            val nowMin = minuteOfDay()
            if (nowMin < cfg.windowStartMin || nowMin >= cfg.windowEndMin) {
                return ModeLibreStatus.OutOfWindow(cfg.windowStartMin)
            }
        }
        // Quota
        val periodStart = when (cfg.frequency) {
            ModeLibreConfig.DAILY -> startOfToday()
            ModeLibreConfig.WEEKLY -> startOfWeek()
            else -> return ModeLibreStatus.NeverConfigured
        }
        val used = modeLibre.sessionCountSince(periodStart)
        if (used >= 1) {
            val next = if (cfg.frequency == ModeLibreConfig.DAILY) startOfToday() + DAY_MS else startOfWeek() + 7 * DAY_MS
            return ModeLibreStatus.QuotaExhausted(next)
        }
        return ModeLibreStatus.Available
    }

    // --- time helpers ---------------------------------------------------------

    private fun nowMillis() = System.currentTimeMillis()
    private fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    fun startOfToday(): Long = startOfDay(nowMillis())
    fun startOfWeek(): Long = Calendar.getInstance().apply {
        timeInMillis = nowMillis()
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    fun startOfMonth(): Long = Calendar.getInstance().apply {
        timeInMillis = nowMillis()
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun minuteOfDay(): Int = Calendar.getInstance().let {
        it.get(Calendar.HOUR_OF_DAY) * 60 + it.get(Calendar.MINUTE)
    }

    companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000

        @Volatile private var instance: NdsRepository? = null
        fun get(context: Context): NdsRepository = instance ?: synchronized(this) {
            instance ?: NdsRepository(NdsDatabase.get(context)).also { instance = it }
        }
    }
}
