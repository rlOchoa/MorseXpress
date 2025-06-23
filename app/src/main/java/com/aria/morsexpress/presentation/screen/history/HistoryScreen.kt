package com.aria.morsexpress.presentation.screen.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aria.morsexpress.data.local.entity.HistoryEntity
import com.aria.morsexpress.presentation.viewmodel.HistoryViewModel
import com.aria.morsexpress.util.EntryType
import com.aria.morsexpress.util.decodeBase64ToBitmap
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val historyList by viewModel.filteredHistory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val sortDescending by viewModel.sortDescending.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("Buscar") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { viewModel.toggleSortOrder() }) {
                Icon(Icons.Default.Sort, contentDescription = "Ordenar")
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar todo")
            }
        }

        FilterChips(filterType = filterType, onFilterSelected = { viewModel.setFilterType(it) })

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(historyList) { item ->
                HistoryItem(
                    entity = item,
                    onDelete = { viewModel.deleteItem(item) }
                )
                Divider()
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar historial") },
            text = { Text("¿Estás seguro de que deseas eliminar todo el historial?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.deleteAll()
                        showDeleteDialog = false
                    }
                }) {
                    Text("Eliminar todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun HistoryItem(entity: HistoryEntity, onDelete: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text("Tipo: ${entity.type}", style = MaterialTheme.typography.labelMedium)
        Text("Texto original: ${entity.originalText}")
        Text("Traducción Morse: ${entity.translatedText}")
        entity.imageBase64?.let {
            decodeBase64ToBitmap(it)?.let { bitmap ->
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Imagen relacionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
fun FilterChips(filterType: EntryType, onFilterSelected: (EntryType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
        EntryType.values().forEach { type ->
            FilterChip(
                selected = filterType == type,
                onClick = { onFilterSelected(type) },
                label = { Text(type.name) }
            )
        }
    }
}