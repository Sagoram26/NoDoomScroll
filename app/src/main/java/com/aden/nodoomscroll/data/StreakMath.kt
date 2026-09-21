package com.aden.nodoomscroll.data

/** Pure streak/milestone arithmetic, split out so it's unit-testable without Room/Context. */
object StreakMath {
    val MILESTONES = listOf(7, 14, 30, 60, 90, 180, 365)

    /**
     * Given the current streak [days] and the highest milestone [alreadyNotified], returns the newly
     * crossed milestone to notify (or null), and the milestone value to persist as "notified".
     * A reset (days dropped below alreadyNotified) rebases notified to the highest ≤ days.
     */
    fun evaluate(days: Int, alreadyNotified: Int): Pair<Int?, Int> {
        val newly = MILESTONES.filter { it in (alreadyNotified + 1)..days }.maxOrNull()
        if (newly != null) return newly to newly
        if (days < alreadyNotified) return null to (MILESTONES.filter { it <= days }.maxOrNull() ?: 0)
        return null to alreadyNotified
    }
}
