package com.aden.nodoomscroll.notif

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.aden.nodoomscroll.data.NdsRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WeeklyReportWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repo = NdsRepository.get(applicationContext)
        val settings = repo.getSettings()
        if (!settings.weeklyReportEnabled) return Result.success()

        val weekStart = repo.startOfWeek()
        val blocked = repo.attempts.blockedCountSinceOnce(weekStart)
        val streak = repo.streak.get()?.currentStreakDays ?: 0
        Notifications.showWeekly(applicationContext, streak, blocked, trendUp = true)
        return Result.success()
    }

    companion object {
        private const val NAME = "weekly_report"

        /** Schedule (or reschedule) the weekly report for the given ISO day + hour. */
        fun schedule(context: Context, isoDay: Int, hour: Int) {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, isoToCalendarDay(isoDay))
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                if (before(now)) add(Calendar.WEEK_OF_YEAR, 1)
            }
            val initialDelay = target.timeInMillis - now.timeInMillis
            val request = PeriodicWorkRequestBuilder<WeeklyReportWorker>(7, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        }

        private fun isoToCalendarDay(iso: Int): Int = when (iso) {
            7 -> Calendar.SUNDAY
            else -> iso + 1 // ISO Mon=1 → Calendar Mon=2
        }
    }
}
