package com.aden.nodoomscroll.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aden.nodoomscroll.R

/**
 * Two local notifications from the PRD: streak-milestone (event-driven) and weekly report
 * (WorkManager). One channel each so the user can tune them independently.
 */
object Notifications {
    private const val CHANNEL_MILESTONE = "streak_milestone"
    private const val CHANNEL_WEEKLY = "weekly_report"

    fun ensureChannels(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_MILESTONE, "Jalons de série", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Notifications aux paliers de jours sans scroll" }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_WEEKLY, "Rapport hebdomadaire", NotificationManager.IMPORTANCE_LOW)
                .apply { description = "Bilan hebdomadaire de tes stats" }
        )
    }

    fun showMilestone(context: Context, days: Int) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        val n = NotificationCompat.Builder(context, CHANNEL_MILESTONE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$days jours sans scroll 🌿")
            .setContentText(milestoneMessage(days))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(1000 + days, n)
    }

    fun showWeekly(context: Context, streakDays: Int, blocked: Int, trendUp: Boolean) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        val trend = if (trendUp) "↑ Mieux qu'avant" else "↓ En baisse"
        val n = NotificationCompat.Builder(context, CHANNEL_WEEKLY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tes stats de cette semaine")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "Streak : $streakDays jours\nBloquées : $blocked tentatives\nTendance : $trend"
            ))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(2000, n)
    }

    private fun milestoneMessage(days: Int): String = when (days) {
        7 -> "Une semaine complète. Continue comme ça."
        14 -> "Deux semaines. L'habitude s'installe."
        30 -> "Un mois entier sans scroll infini."
        60 -> "Deux mois. Impressionnant."
        90 -> "Trois mois. C'est une nouvelle normalité."
        180 -> "Six mois. Tu as repris le contrôle."
        365 -> "Un an. Bravo."
        else -> "Beau progrès."
    }
}
