package com.example.liliplayer.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.liliplayer.domain.model.SortOrder
import com.example.liliplayer.ui.theme.Primary

@Composable
fun SortMenuDropdown(
    currentSort: SortOrder,
    onSortSelected: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.Sort, contentDescription = "Sort")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        SortOrder.entries.forEach { order ->
            DropdownMenuItem(
                text = { Text(order.displayName) },
                onClick = {
                    onSortSelected(order)
                    expanded = false
                },
                leadingIcon = if (order == currentSort) {
                    { Icon(Icons.Default.Check, contentDescription = null, tint = Primary) }
                } else null
            )
        }
    }
}
