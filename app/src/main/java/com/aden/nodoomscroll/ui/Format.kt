package com.aden.nodoomscroll.ui

import java.util.Calendar

/** French time / date formatting used across screens + widget. */
object Format {

    /** Seconds → "1h 45" / "45 min" / "0 min". */
    fun duration(totalSeconds: Long): String {
        val totalMin = totalSeconds / 60
        val h = totalMin / 60
        val m = totalMin % 60
        return if (h > 0) "${h}h ${m.toString().padStart(2, '0')}" else "$m min"
    }

    /** Long spans → "187 j 16 h 45". */
    fun longDuration(totalSeconds: Long): String {
        val totalMin = totalSeconds / 60
        val days = totalMin / (60 * 24)
        val h = (totalMin / 60) % 24
        val m = totalMin % 60
        return "$days j $h h ${m.toString().padStart(2, '0')}"
    }

    private val months = listOf(
        "janvier", "février", "mars", "avril", "mai", "juin",
        "juillet", "août", "septembre", "octobre", "novembre", "décembre",
    )
    private val shortDays = listOf("lun.", "mar.", "mer.", "jeu.", "ven.", "sam.", "dim.")
    val fullDays = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche")
    val dayAbbrev = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")

    /** "mardi 25 juin" — today's greeting date. */
    fun today(): String {
        val c = Calendar.getInstance()
        val dow = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Mon=0
        val lower = fullDays[dow].lowercase()
        return "$lower ${c.get(Calendar.DAY_OF_MONTH)} ${months[c.get(Calendar.MONTH)]}"
    }

    /** epoch millis → "23 juin 2024". */
    fun date(millis: Long): String {
        val c = Calendar.getInstance().apply { timeInMillis = millis }
        return "${c.get(Calendar.DAY_OF_MONTH)} ${months[c.get(Calendar.MONTH)]} ${c.get(Calendar.YEAR)}"
    }

    /** epoch millis → "sam. 14h30" (bypass log). */
    fun dayTime(millis: Long): String {
        val c = Calendar.getInstance().apply { timeInMillis = millis }
        val dow = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Calendar SUN=1 → index; Mon=0
        val h = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)
        return "${shortDays[dow]} ${h}h${m.toString().padStart(2, '0')}"
    }

    /** "Dimanche 20h" from ISO day (1=Mon..7=Sun) + hour. */
    fun weeklySlot(isoDay: Int, hour: Int): String {
        val name = fullDays[(isoDay - 1).coerceIn(0, 6)]
        return "$name ${hour}h"
    }
}
