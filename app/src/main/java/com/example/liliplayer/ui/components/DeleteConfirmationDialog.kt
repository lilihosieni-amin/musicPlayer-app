package com.example.liliplayer.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.liliplayer.ui.theme.*

@Composable
fun DeleteConfirmationDialog(
    songTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = BgCard,
        title = { Text("Delete Song") },
        text = { Text("Are you sure you want to delete \"$songTitle\" from your device? This cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = BgCard)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
