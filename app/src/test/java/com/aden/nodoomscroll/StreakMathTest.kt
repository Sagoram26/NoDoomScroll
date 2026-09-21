package com.aden.nodoomscroll

import com.aden.nodoomscroll.data.StreakMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StreakMathTest {
    @Test fun crossesFirstMilestone() {
        val (crossed, notified) = StreakMath.evaluate(days = 7, alreadyNotified = 0)
        assertEquals(7, crossed)
        assertEquals(7, notified)
    }

    @Test fun noCrossBetweenMilestones() {
        val (crossed, notified) = StreakMath.evaluate(days = 10, alreadyNotified = 7)
        assertNull(crossed)
        assertEquals(7, notified)
    }

    @Test fun jumpsToHighestCrossed() {
        // Streak recompute after being away several days: 5 -> 35 crosses 7,14,30; notify only 30.
        val (crossed, notified) = StreakMath.evaluate(days = 35, alreadyNotified = 0)
        assertEquals(30, crossed)
        assertEquals(30, notified)
    }

    @Test fun resetRebasesNotified() {
        // Mode libre used at day 40 → streak drops to 0. notified rebases so 7 can fire again.
        val (crossed, notified) = StreakMath.evaluate(days = 0, alreadyNotified = 30)
        assertNull(crossed)
        assertEquals(0, notified)
    }

    @Test fun reNotifiesAfterReset() {
        val (crossed, notified) = StreakMath.evaluate(days = 7, alreadyNotified = 0)
        assertEquals(7, crossed)
        assertEquals(7, notified)
    }

    @Test fun oneEightyIsAMilestone() {
        val (crossed, _) = StreakMath.evaluate(days = 180, alreadyNotified = 90)
        assertEquals(180, crossed)
    }
}
