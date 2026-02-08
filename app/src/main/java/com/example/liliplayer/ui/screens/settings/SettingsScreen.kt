package com.example.liliplayer.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.liliplayer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgMain)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Playback Section
            SettingsSection(title = "Playback") {
                SettingsToggleItem(
                    title = "Gapless Playback",
                    subtitle = "Remove silence between tracks",
                    defaultValue = true
                )
                SettingsToggleItem(
                    title = "Auto-play",
                    subtitle = "Continue playing when queue ends",
                    defaultValue = false
                )
                SettingsToggleItem(
                    title = "Fade on Play/Pause",
                    subtitle = "Smooth audio transition",
                    defaultValue = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Library Section
            SettingsSection(title = "Library") {
                SettingsActionItem(
                    title = "Rescan Library",
                    subtitle = "Scan device for new music files",
                    onClick = { /* trigger rescan */ }
                )
                SettingsToggleItem(
                    title = "Filter Short Tracks",
                    subtitle = "Hide tracks shorter than 30 seconds",
                    defaultValue = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Section
            SettingsSection(title = "About") {
                SettingsInfoItem(
                    title = "Lili Player",
                    subtitle = "Version 1.0"
                )
                SettingsInfoItem(
                    title = "Built with",
                    subtitle = "Jetpack Compose, Media3, Material 3"
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(24.dp),
            ambientColor = Lavender.copy(alpha = 0.3f),
            spotColor = Lavender.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    defaultValue: Boolean
) {
    var checked by remember { mutableStateOf(defaultValue) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = BgCard,
                checkedTrackColor = Primary,
                uncheckedThumbColor = BgCard,
                uncheckedTrackColor = TextSecondary.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun SettingsActionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = BgCard
            )
        ) {
            Text("Scan")
        }
    }
}

@Composable
fun SettingsInfoItem(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}
