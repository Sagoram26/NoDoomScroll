package com.aden.nodoomscroll.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: BlockAttempt)

    @Query("SELECT COUNT(*) FROM block_attempts WHERE wasBlocked = 1 AND timestampMillis >= :sinceMillis")
    fun blockedCountSince(sinceMillis: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM block_attempts WHERE wasBlocked = 1 AND timestampMillis >= :sinceMillis")
    suspend fun blockedCountSinceOnce(sinceMillis: Long): Int

    @Query("SELECT COUNT(*) FROM block_attempts WHERE timestampMillis >= :sinceMillis")
    fun totalCountSince(sinceMillis: Long): Flow<Int>

    @Query("SELECT * FROM block_attempts WHERE timestampMillis >= :sinceMillis ORDER BY timestampMillis ASC")
    fun attemptsSince(sinceMillis: Long): Flow<List<BlockAttempt>>

    @Query("SELECT * FROM block_attempts WHERE wasBlocked = 0 ORDER BY timestampMillis DESC LIMIT :limit")
    fun recentBypasses(limit: Int): Flow<List<BlockAttempt>>

    @Query("SELECT MIN(timestampMillis) FROM block_attempts")
    suspend fun firstAttemptMillis(): Long?
}

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak_state WHERE id = 0")
    fun observe(): Flow<StreakState?>

    @Query("SELECT * FROM streak_state WHERE id = 0")
    suspend fun get(): StreakState?

    @Upsert
    suspend fun upsert(state: StreakState)
}

@Dao
interface ModeLibreDao {
    @Query("SELECT * FROM mode_libre_config WHERE id = 0")
    fun observeConfig(): Flow<ModeLibreConfig?>

    @Query("SELECT * FROM mode_libre_config WHERE id = 0")
    suspend fun getConfig(): ModeLibreConfig?

    @Upsert
    suspend fun upsertConfig(config: ModeLibreConfig)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ModeLibreSession)

    @Query("SELECT COUNT(*) FROM mode_libre_sessions WHERE timestampMillis >= :sinceMillis")
    suspend fun sessionCountSince(sinceMillis: Long): Int

    @Query("SELECT COUNT(*) FROM mode_libre_sessions WHERE timestampMillis >= :sinceMillis")
    fun sessionCountSinceFlow(sinceMillis: Long): Flow<Int>

    @Query("SELECT MAX(timestampMillis) FROM mode_libre_sessions")
    suspend fun lastSessionMillis(): Long?
}

@Dao
interface BlockedAppDao {
    @Query("SELECT * FROM blocked_apps ORDER BY isNative DESC, appLabel ASC")
    fun observeAll(): Flow<List<BlockedApp>>

    @Query("SELECT * FROM blocked_apps WHERE enabled = 1")
    suspend fun enabled(): List<BlockedApp>

    @Upsert
    suspend fun upsert(app: BlockedApp)

    @Query("SELECT COUNT(*) FROM blocked_apps")
    suspend fun count(): Int
}

@Dao
interface AppEstimateDao {
    @Upsert
    suspend fun upsert(estimate: AppEstimate)

    @Query("SELECT * FROM app_estimates")
    suspend fun all(): List<AppEstimate>

    @Query("SELECT COALESCE(SUM(estimatedDailyMinutes), 0) FROM app_estimates")
    suspend fun totalDailyMinutes(): Int
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 0")
    fun observe(): Flow<Settings?>

    @Query("SELECT * FROM settings WHERE id = 0")
    suspend fun get(): Settings?

    @Upsert
    suspend fun upsert(settings: Settings)
}
