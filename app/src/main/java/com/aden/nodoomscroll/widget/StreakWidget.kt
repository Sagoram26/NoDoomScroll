package com.aden.nodoomscroll.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aden.nodoomscroll.data.NdsRepository
import com.aden.nodoomscroll.ui.Format

/**
 * 2×2 home-screen widget: streak days + time saved today. Reads the repo directly (same process).
 * The host refreshes on its own cadence; call StreakWidget().updateAll(context) after a data change
 * to push an immediate update (e.g. streak roll at midnight).
 */
class StreakWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = NdsRepository.get(context)
        val days = repo.streak.get()?.currentStreakDays ?: 0
        val blockedToday = repo.attempts.blockedCountSinceOnce(repo.startOfToday())
        val savedSecs = repo.secondsSaved(blockedToday)
        provideContent { WidgetContent(days, savedSecs) }
    }

    @Composable
    private fun WidgetContent(days: Int, savedSecs: Long) {
        val bg = Color(0xFF0C1611)
        val accent = Color(0xFF9CC4AC)
        val secondary = Color(0xFFA7B8AE)
        Column(
            GlanceModifier.fillMaxSize().background(bg).cornerRadius(24.dp).padding(16.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Text("$days", style = TextStyle(color = ColorProvider(accent), fontSize = 40.sp, fontWeight = FontWeight.Bold))
            Text("jours sans scroll", style = TextStyle(color = ColorProvider(secondary), fontSize = 12.sp))
            Text(Format.duration(savedSecs), style = TextStyle(color = ColorProvider(accent), fontSize = 16.sp, fontWeight = FontWeight.Bold))
            Text("gagné aujourd'hui", style = TextStyle(color = ColorProvider(secondary), fontSize = 11.sp))
        }
    }
}

class StreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StreakWidget()
}
