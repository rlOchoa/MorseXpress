package com.aria.morsexpress.presentation.screen.history

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.aria.morsexpress.data.local.entity.TranslationEntity
import com.aria.morsexpress.presentation.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val historyList by viewModel.filteredHistory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val sortDescending by viewModel.sortDescending.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                HistoryItem(entity = item, onDelete = { viewModel.deleteItem(item) })
                Divider()
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar historial") },
            text = { Text("¿Deseas eliminar todo el historial?") },
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
fun HistoryItem(entity: TranslationEntity, onDelete: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text("Tipo: ${entity.inputType}", style = MaterialTheme.typography.labelMedium)
//        Text("Fecha: ${entity.timestamp}", style = MaterialTheme.typography.labelMedium)
        if (entity.inputType == "MORSE" || entity.inputType == "TEXT") {
            Text("Original: ${entity.originalText}")
        } else if (entity.inputType == "AUDIO") {
            Text("Audio: ${entity.inputPathOrContent}")
        } else if (entity.inputType == "MORSE_IMAGE") {
            Text("Original: ${entity.originalText}")
//            Text("Imagen Morse: ${entity.inputPathOrContent}",style = MaterialTheme.typography.labelMedium)
        } else if (entity.inputType == "OCR") {
            Text("Original: ${entity.originalText}")
//            Text("Imagen OCR: ${entity.inputPathOrContent}",style = MaterialTheme.typography.labelMedium)
        }
        Text("Traducción: ${entity.translatedText}")


        if (entity.inputType == "OCR" || entity.inputType == "MORSE_IMAGE") {
            val uri = runCatching { Uri.parse(entity.inputPathOrContent) }.getOrNull()
            uri?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(model = it),
                    contentDescription = "Imagen OCR/Morse",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
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
fun FilterChips(filterType: String, onFilterSelected: (String) -> Unit) {
    val options = listOf("TODOS", "TEXT", "OCR", "MORSE_IMAGE", "AUDIO")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        options.forEach { type ->
            FilterChip(
                selected = filterType == type,
                onClick = { onFilterSelected(type) },
                label = { Text(type) }
            )
        }
    }
}