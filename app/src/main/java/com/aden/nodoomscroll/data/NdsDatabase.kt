package com.aden.nodoomscroll.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BlockAttempt::class,
        StreakState::class,
        ModeLibreConfig::class,
        ModeLibreSession::class,
        BlockedApp::class,
        AppEstimate::class,
        Settings::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class NdsDatabase : RoomDatabase() {
    abstract fun blockAttemptDao(): BlockAttemptDao
    abstract fun streakDao(): StreakDao
    abstract fun modeLibreDao(): ModeLibreDao
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun appEstimateDao(): AppEstimateDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var instance: NdsDatabase? = null

        fun get(context: Context): NdsDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                NdsDatabase::class.java,
                "nodoomscroll.db",
            ).build().also { instance = it }
        }
    }
}
