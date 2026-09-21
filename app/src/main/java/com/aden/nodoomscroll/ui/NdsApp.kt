package com.aden.nodoomscroll.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aden.nodoomscroll.ui.theme.*

/**
 * Flat hub navigation matching the mockups: Home has 3 top-right icons → sub-screens; each
 * sub-screen has a back button → Home. No bottom nav bar.
 */
private enum class Screen { Home, Analytics, Apps, Settings }

@Composable
fun NdsApp() {
    val vm: AppViewModel = viewModel()
    val settings by vm.settings.collectAsState()

    // null = still loading; wait before deciding onboarding gate to avoid a flash.
    val onboardingDone = settings?.onboardingComplete

    NdsBackground {
        when (onboardingDone) {
            null -> Box(Modifier.fillMaxSize()) // loading
            false -> OnboardingScreen(vm, onComplete = { /* settings flow flips to true → recompose */ })
            true -> MainShell(vm)
        }
    }
}

@Composable
private fun MainShell(vm: AppViewModel) {
    var screen by remember { mutableStateOf(Screen.Home) }
    val back = { screen = Screen.Home }

    when (screen) {
        Screen.Home -> HomeScreen(
            vm,
            onNavAnalytics = { screen = Screen.Analytics },
            onNavApps = { screen = Screen.Apps },
            onNavSettings = { screen = Screen.Settings },
        )
        Screen.Analytics -> SubScreen("Tes stats", back) { AnalyticsScreen(vm) }
        Screen.Apps -> SubScreen("Apps filtrées", back) { AppsScreen(vm) }
        Screen.Settings -> SubScreen("Réglages", back) { SettingsScreen(vm) }
    }
}

/** Shared top bar (back chevron) for sub-screens; the screen's own title stays inside its content. */
@Composable
private fun SubScreen(contentDesc: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    val c = LocalNdsColors.current
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Space.s4, vertical = Space.s3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(Radius.pill)).glass(Radius.pill)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = c.textPrimary, modifier = Modifier.size(20.dp))
            }
        }
        Box(Modifier.weight(1f)) { content() }
    }
}

