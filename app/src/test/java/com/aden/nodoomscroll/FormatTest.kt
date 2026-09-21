package com.aden.nodoomscroll

import com.aden.nodoomscroll.ui.Format
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatTest {
    @Test fun duration_hoursAndMinutes() {
        assertEquals("1h 45", Format.duration((1 * 3600 + 45 * 60).toLong()))
    }

    @Test fun duration_minutesOnly() {
        assertEquals("45 min", Format.duration((45 * 60).toLong()))
        assertEquals("0 min", Format.duration(0))
    }

    @Test fun duration_padsMinutes() {
        assertEquals("2h 05", Format.duration((2 * 3600 + 5 * 60).toLong()))
    }

    @Test fun longDuration_days() {
        // 187 j 16 h 45 (all-time example from analytics.md)
        val secs = (187L * 24 * 60 + 16L * 60 + 45L) * 60
        assertEquals("187 j 16 h 45", Format.longDuration(secs))
    }

    @Test fun weeklySlot_default() {
        assertEquals("Dimanche 20h", Format.weeklySlot(7, 20))
    }
}
