package com.example.liliplayer.ui.screens.equalizer

import android.content.Context
import android.media.audiofx.Equalizer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.liliplayer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    onBack: () -> Unit
) {
    var enabled by remember { mutableStateOf(false) }
    val bandLevels = remember { mutableStateListOf(0f, 0f, 0f, 0f, 0f) }
    val bandLabels = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
    val presets = listOf("Custom", "Flat", "Bass Boost", "Rock", "Pop", "Jazz", "Classical")
    var selectedPreset by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equalizer") },
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
            // Enable/Disable
            Card(
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Primary.copy(alpha = 0.2f),
                    spotColor = Primary.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Equalizer",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BgCard,
                            checkedTrackColor = Primary,
                            uncheckedThumbColor = BgCard,
                            uncheckedTrackColor = TextSecondary.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preset Dropdown
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
                        text = "Preset",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = presets[selectedPreset],
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            presets.forEachIndexed { index, preset ->
                                DropdownMenuItem(
                                    text = { Text(preset) },
                                    onClick = {
                                        selectedPreset = index
                                        expanded = false
                                        when (index) {
                                            1 -> bandLevels.indices.forEach { bandLevels[it] = 0f }
                                            2 -> { bandLevels[0] = 0.6f; bandLevels[1] = 0.4f; bandLevels[2] = 0f; bandLevels[3] = 0f; bandLevels[4] = 0f }
                                            3 -> { bandLevels[0] = 0.4f; bandLevels[1] = 0.2f; bandLevels[2] = -0.1f; bandLevels[3] = 0.3f; bandLevels[4] = 0.5f }
                                            4 -> { bandLevels[0] = -0.1f; bandLevels[1] = 0.3f; bandLevels[2] = 0.4f; bandLevels[3] = 0.2f; bandLevels[4] = -0.1f }
                                            5 -> { bandLevels[0] = 0.3f; bandLevels[1] = 0f; bandLevels[2] = 0.1f; bandLevels[3] = -0.1f; bandLevels[4] = 0.2f }
                                            6 -> { bandLevels[0] = -0.1f; bandLevels[1] = -0.1f; bandLevels[2] = 0f; bandLevels[3] = 0.2f; bandLevels[4] = 0.3f }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Band Sliders
            Card(
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = SkyBlue.copy(alpha = 0.3f),
                    spotColor = SkyBlue.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bands",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    bandLabels.forEachIndexed { index, label ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.width(60.dp)
                            )
                            Slider(
                                value = bandLevels[index],
                                onValueChange = {
                                    bandLevels[index] = it
                                    selectedPreset = 0
                                },
                                valueRange = -1f..1f,
                                modifier = Modifier.weight(1f),
                                enabled = enabled,
                                colors = SliderDefaults.colors(
                                    thumbColor = Primary,
                                    activeTrackColor = Lavender,
                                    inactiveTrackColor = SeekBarTrack
                                )
                            )
                            Text(
                                text = "${(bandLevels[index] * 10).toInt()} dB",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.width(50.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
