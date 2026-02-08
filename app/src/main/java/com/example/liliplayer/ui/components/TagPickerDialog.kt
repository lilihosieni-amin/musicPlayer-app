package com.example.liliplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.data.local.entity.TagEntity
import com.example.liliplayer.ui.screens.tags.TagsViewModel
import com.example.liliplayer.ui.theme.*

@Composable
fun TagPickerDialog(
    songId: Long,
    songTagIds: List<Long>,
    onDismiss: () -> Unit,
    onToggleTag: (Long, Boolean) -> Unit,
    viewModel: TagsViewModel = hiltViewModel()
) {
    val allTags by viewModel.tags.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Tags") },
        shape = RoundedCornerShape(24.dp),
        containerColor = BgCard,
        text = {
            if (allTags.isEmpty()) {
                Text("No tags created yet. Create tags from the Tags screen.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(allTags) { tag ->
                        val isAssigned = songTagIds.contains(tag.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTag(tag.id, !isAssigned) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isAssigned,
                                onCheckedChange = { onToggleTag(tag.id, it) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                modifier = Modifier.size(24.dp),
                                shape = CircleShape,
                                color = try { Color(android.graphics.Color.parseColor(tag.color)) } catch (e: Exception) { Primary }
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tag.name, color = TextPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        },
        dismissButton = {}
    )
}
