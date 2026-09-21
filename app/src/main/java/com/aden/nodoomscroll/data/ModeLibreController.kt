package com.aden.nodoomscroll.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Process-wide mode-libre runtime state, shared between the UI (starts a session) and the
 * AccessibilityService (suspends blocking while a session is live). The service and Activity run in
 * the same process, so an in-memory StateFlow is enough — no IPC. Persistence of *consumption*
 * (quota, streak reset) is the repository's job; this only holds the live window.
 */
object ModeLibreController {

    data class State(
        val phase: Phase = Phase.Idle,
        /** epoch millis when the current phase ends (delay end, or session end). */
        val phaseEndsAt: Long = 0L,
    )

    enum class Phase { Idle, Delay, Active }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    /** True while blocking should be SUSPENDED (session running). Delay phase still blocks. */
    fun isUnlocked(now: Long): Boolean {
        val s = _state.value
        return s.phase == Phase.Active && now < s.phaseEndsAt
    }

    fun beginDelay(delayEndsAt: Long) {
        _state.value = State(Phase.Delay, delayEndsAt)
    }

    fun beginSession(sessionEndsAt: Long) {
        _state.value = State(Phase.Active, sessionEndsAt)
    }

    fun end() {
        _state.value = State(Phase.Idle, 0L)
    }
}
