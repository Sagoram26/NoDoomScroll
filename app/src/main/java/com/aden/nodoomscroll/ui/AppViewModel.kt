package com.aden.nodoomscroll.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aden.nodoomscroll.data.BlockAttempt
import com.aden.nodoomscroll.data.BlockedApp
import com.aden.nodoomscroll.data.ModeLibreConfig
import com.aden.nodoomscroll.data.ModeLibreController
import com.aden.nodoomscroll.data.NdsRepository
import com.aden.nodoomscroll.data.Settings
import com.aden.nodoomscroll.data.StreakState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = NdsRepository.get(application)

    init {
        // Surface any milestone crossed while the app was closed (streak grows on clean days, but
        // refreshStreak only runs on block events otherwise). ponytail: a daily WorkManager tick at
        // local midnight would make this exact; app-open refresh is the lazy-correct version.
        viewModelScope.launch {
            repo.refreshStreak()?.let { m ->
                com.aden.nodoomscroll.notif.Notifications.showMilestone(getApplication(), m)
            }
        }
    }

    val streak: StateFlow<StreakState?> = repo.observeStreak()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val settings: StateFlow<Settings?> = repo.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val modeLibreConfig: StateFlow<ModeLibreConfig?> = repo.observeModeLibreConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val apps: StateFlow<List<BlockedApp>> = repo.observeApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedToday: StateFlow<Int> = repo.blockedTodayFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val secondsSavedToday: StateFlow<Long> = repo.secondsSavedTodayFlow()
        .map { repo.secondsSaved(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val trend24h: StateFlow<List<Int>> = repo.attemptsTodayFlow()
        .map { attempts ->
            // bucket attempts into 24h sparkline
            val buckets = IntArray(24)
            val start = repo.startOfToday()
            for (a in attempts) {
                val hour = ((a.timestampMillis - start) / (60 * 60 * 1000)).toInt().coerceIn(0, 23)
                buckets[hour]++
            }
            buckets.toList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val modeLibreStatus: StateFlow<NdsRepository.ModeLibreStatus> = flow {
        while (true) {
            emit(repo.modeLibreStatus())
            kotlinx.coroutines.delay(60_000) // refresh every minute
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NdsRepository.ModeLibreStatus.NeverConfigured)

    // Analytics lists
    val bypassHistory: StateFlow<List<BlockAttempt>> = repo.recentBypassesFlow(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val perAppToday: StateFlow<List<Pair<String, Int>>> = repo.attemptsTodayFlow()
        .map { list -> list.groupBy { it.appLabel }.map { it.key to it.value.size }.sortedByDescending { it.second } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All-time derived
    val allTimeSecondsSaved: StateFlow<Long> = repo.attempts.blockedCountSince(0)
        .map { repo.secondsSaved(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val weekBlocked: StateFlow<Int> = repo.attempts.blockedCountSince(repo.startOfWeek())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weekSecondsSaved: StateFlow<Long> = repo.attempts.blockedCountSince(repo.startOfWeek())
        .map { repo.secondsSaved(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val monthSecondsSaved: StateFlow<Long> = repo.attempts.blockedCountSince(repo.startOfMonth())
        .map { repo.secondsSaved(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val allTimeSessions: StateFlow<Int> = repo.modeLibre.sessionCountSinceFlow(0)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Block rate over all time (blocked / total detected), as 0..100. */
    val blockRate: StateFlow<Int> = combine(
        repo.attempts.blockedCountSince(0), repo.attempts.totalCountSince(0)
    ) { blocked, total -> if (total == 0) 100 else (blocked * 100 / total) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    /**
     * Mode-libre lifecycle: activation delay (blocking stays up, "le temps que l'envie passe") →
     * session window (blocking suspended) → auto-close (blocking resumes, quota consumed, streak
     * reset). Runs on the VM scope; the AccessibilityService reads ModeLibreController live.
     */
    fun startModeLibre() {
        // ponytail: session timing lives on the VM scope. If the process is killed mid-window the
        // quota/streak-reset won't fire. Fine for a personal app; promote to a foreground timer or
        // WorkManager if that edge matters.
        viewModelScope.launch {
            val cfg = repo.modeLibre.getConfig() ?: return@launch
            if (repo.modeLibreStatus() !is NdsRepository.ModeLibreStatus.Available) return@launch

            val now = System.currentTimeMillis()
            if (cfg.activationDelayMin > 0) {
                val delayMs = cfg.activationDelayMin * 60_000L
                ModeLibreController.beginDelay(now + delayMs)
                kotlinx.coroutines.delay(delayMs)
            }
            val sessionMs = cfg.durationMin * 60_000L
            ModeLibreController.beginSession(System.currentTimeMillis() + sessionMs)
            kotlinx.coroutines.delay(sessionMs)
            ModeLibreController.end()
            repo.useModeLibreSession() // consume quota + reset streak
        }
    }

    val modeLibreRuntime: StateFlow<ModeLibreController.State> = ModeLibreController.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ModeLibreController.State())

    fun setGrayscale(enabled: Boolean) {
        viewModelScope.launch { repo.updateSettings { it.copy(grayscaleEnabled = enabled) } }
    }

    fun addCustomApp(pkg: String, label: String, grayscaleOnly: Boolean) {
        viewModelScope.launch {
            repo.apps.upsert(
                BlockedApp(
                    packageName = pkg,
                    appLabel = label,
                    blockType = if (grayscaleOnly) BlockedApp.GRAYSCALE_ONLY else BlockedApp.TOTAL,
                    blockedSectionsDesc = if (grayscaleOnly) "Niveaux de gris" else "Tout",
                    isNative = false,
                    enabled = true,
                    addedTimestamp = System.currentTimeMillis(),
                )
            )
        }
    }

    fun completeOnboarding(config: ModeLibreConfig) {
        viewModelScope.launch {
            repo.modeLibre.upsertConfig(config.copy(id = 0, isLocked = true))
            repo.seedNativeApps()
            repo.activate()
            repo.updateSettings { it.copy(onboardingComplete = true) }
            val app = getApplication<Application>()
            com.aden.nodoomscroll.notif.Notifications.ensureChannels(app)
            val s = repo.getSettings()
            com.aden.nodoomscroll.notif.WeeklyReportWorker.schedule(app, s.weeklyReportDay, s.weeklyReportHour)
        }
    }
}
