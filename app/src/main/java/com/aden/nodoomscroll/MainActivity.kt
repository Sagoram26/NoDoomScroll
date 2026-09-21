package com.aden.nodoomscroll

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aden.nodoomscroll.ui.theme.NoDoomScrollTheme

class MainActivity : ComponentActivity() {
    private val requestNotif = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { /* result ignored; notifications are optional */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.aden.nodoomscroll.notif.Notifications.ensureChannels(this)
        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestNotif.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            NoDoomScrollTheme {
                com.aden.nodoomscroll.ui.NdsApp()
            }
        }
    }
}

@Composable
private fun DebugScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lines by EventLog.lines.collectAsState()
    var enabled by remember { mutableStateOf(isServiceEnabled(context)) }
    var mode by remember { mutableStateOf(BlocklistManager.mode) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (enabled) "Service ACTIVÉ" else "Service INACTIF",
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            fontSize = 18.sp,
        )
        Text("Mode : $mode", fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = {
                context.startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }) { Text("Réglages") }

            Button(onClick = { enabled = isServiceEnabled(context) }) { Text("Rafraîchir") }

            Button(onClick = {
                BlocklistManager.mode =
                    if (BlocklistManager.mode == BlocklistManager.Mode.BLOCK_WHOLE_APP)
                        BlocklistManager.Mode.SELECTIVE
                    else
                        BlocklistManager.Mode.BLOCK_WHOLE_APP
                mode = BlocklistManager.mode
            }) { Text("Mode") }
        }

        Text(
            text = "Events (${lines.size})",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(lines) { line ->
                Text(text = line, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        }
    }
}

/** Is our AccessibilityService currently enabled in Settings? */
private fun isServiceEnabled(context: Context): Boolean {
    val expected = "${context.packageName}/${NoDoomScrollService::class.java.name}"
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
    ) ?: return false
    val splitter = TextUtils.SimpleStringSplitter(':')
    splitter.setString(enabledServices)
    while (splitter.hasNext()) {
        if (splitter.next().equals(expected, ignoreCase = true)) return true
    }
    return false
}
