package com.example.liliplayer.ui.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.liliplayer.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    onPermissionGranted: @Composable () -> Unit
) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)

    // Check MANAGE_EXTERNAL_STORAGE for file deletion on API 30+
    val context = LocalContext.current
    var hasManageStorage by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else true
        )
    }

    // Re-check when returning from settings
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasManageStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Environment.isExternalStorageManager()
                } else true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (permissionState.status.isGranted && hasManageStorage) {
        onPermissionGranted()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Primary.copy(alpha = 0.2f),
                        spotColor = Secondary.copy(alpha = 0.15f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Permission Required",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!permissionState.status.isGranted) {
                        Text(
                            text = "Lili Player needs access to your music library to play songs.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { permissionState.launchPermissionRequest() },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = BgCard
                            )
                        ) {
                            Text(
                                "Grant Music Access",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    } else if (!hasManageStorage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Text(
                            text = "Lili Player needs file management access to delete and edit music files.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = BgCard
                            )
                        ) {
                            Text(
                                "Grant File Access",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
