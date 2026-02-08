package com.example.liliplayer.ui.screens.tags

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.ui.components.EmptyState
import com.example.liliplayer.ui.theme.*

@Composable
fun TagsScreen(
    onTagClick: (Long) -> Unit,
    viewModel: TagsViewModel = hiltViewModel()
) {
    val tags by viewModel.tags.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Secondary,
                contentColor = TextPrimary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Tag")
            }
        }
    ) { innerPadding ->
        if (tags.isEmpty()) {
            EmptyState(
                message = "No tags yet\nTap + to create one",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(tags, key = { it.id }) { tag ->
                    val tagColor = try { Color(android.graphics.Color.parseColor(tag.color)) } catch (e: Exception) { Primary }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = tagColor.copy(alpha = 0.4f),
                                spotColor = tagColor.copy(alpha = 0.3f)
                            )
                            .clickable { onTagClick(tag.id) },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = try { Color(android.graphics.Color.parseColor(tag.color)) } catch (e: Exception) { Primary }
                            ) {
                                Icon(
                                    Icons.Default.Label,
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp),
                                    tint = BgCard
                                )
                            }
                            Text(
                                text = tag.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            )
                            IconButton(onClick = { viewModel.deleteTag(tag.id) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTagDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, color ->
                viewModel.createTag(name, color)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreateTagDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val colors = listOf("#6941C6", "#FFB800", "#FF6B9D", "#4CAF50", "#2196F3", "#FF5722")
    var selectedColor by remember { mutableStateOf(colors[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = BgCard,
        title = { Text("Create Tag", style = MaterialTheme.typography.headlineMedium) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tag name") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Color", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { color ->
                        Surface(
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { selectedColor = color },
                            shape = CircleShape,
                            color = try { Color(android.graphics.Color.parseColor(color)) } catch (e: Exception) { Primary },
                            border = if (selectedColor == color) BorderStroke(3.dp, TextPrimary) else null
                        ) {}
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name.trim(), selectedColor) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary, contentColor = TextPrimary),
                enabled = name.isNotBlank()
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
