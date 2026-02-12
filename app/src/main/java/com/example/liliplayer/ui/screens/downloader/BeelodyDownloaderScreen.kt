package com.example.liliplayer.ui.screens.downloader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.data.repository.BeelodyRepository.DownloadState
import com.example.liliplayer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeelodyDownloaderScreen(
    onBack: () -> Unit,
    viewModel: BeelodyDownloaderViewModel = hiltViewModel()
) {
    val url by viewModel.url.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Beelody") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Card
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
                        text = "Download",
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = url,
                        onValueChange = { viewModel.setUrl(it) },
                        label = { Text("Beelody Link") },
                        placeholder = { Text("Paste link here") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderDark,
                            cursorColor = Primary,
                            focusedLabelColor = Primary
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                clipboardManager.getText()?.text?.let { viewModel.setUrl(it) }
                            }) {
                                Icon(
                                    Icons.Default.ContentPaste,
                                    contentDescription = "Paste",
                                    tint = Primary
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.download() },
                        enabled = url.isNotBlank() && !viewModel.isDownloading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = BgCard
                        )
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download")
                    }
                }
            }

            // Status Card
            AnimatedVisibility(
                visible = downloadState !is DownloadState.Idle,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                when (val state = downloadState) {
                    is DownloadState.Idle -> {}
                    is DownloadState.LoggingIn -> StatusCard("Logging in...")
                    is DownloadState.ParsingPage -> StatusCard("Parsing page...")
                    is DownloadState.Downloading -> ProgressCard(
                        filename = state.currentFile,
                        progress = state.progress,
                        label = if (state.totalFiles > 1) "File ${state.currentIndex}/${state.totalFiles}" else null
                    )
                    is DownloadState.Extracting -> StatusCard("Extracting ZIP...")
                    is DownloadState.Scanning -> StatusCard("Scanning media...")
                    is DownloadState.Success -> SuccessCard(
                        fileCount = state.fileCount,
                        onDone = { viewModel.reset() }
                    )
                    is DownloadState.Error -> ErrorCard(
                        message = state.message,
                        onRetry = { viewModel.download() }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Lavender.copy(alpha = 0.3f),
                spotColor = Lavender.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp,
                color = Primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ProgressCard(
    filename: String,
    progress: Int,
    label: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
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
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = filename,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Primary,
                trackColor = SeekBarTrack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$progress%",
                style = MaterialTheme.typography.labelLarge,
                color = Primary
            )
        }
    }
}

@Composable
private fun SuccessCard(fileCount: Int, onDone: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MintGreen.copy(alpha = 0.3f),
                spotColor = MintGreen.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MintLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$fileCount file${if (fileCount != 1) "s" else ""} downloaded",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onDone,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = BgCard
                )
            ) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = ErrorRed.copy(alpha = 0.3f),
                spotColor = ErrorRed.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PinkLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = BgCard
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}
