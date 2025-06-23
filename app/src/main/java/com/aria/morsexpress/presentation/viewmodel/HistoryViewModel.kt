package com.aria.morsexpress.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aria.morsexpress.data.local.database.AppDatabase
import com.aria.morsexpress.data.local.entity.TranslationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).translationDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow("TODOS")
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _sortDescending = MutableStateFlow(true)
    val sortDescending: StateFlow<Boolean> = _sortDescending.asStateFlow()

    val filteredHistory: StateFlow<List<TranslationEntity>> = combine(
        dao.getAllTranslations(),
        _searchQuery,
        _filterType,
        _sortDescending
    ) { allItems, query, type, desc ->
        var items = allItems

        if (type != "TODOS") {
            items = items.filter { it.inputType == type }
        }

        if (query.isNotBlank()) {
            items = items.filter {
                it.inputPathOrContent.contains(query, ignoreCase = true) ||
                        it.translatedText.contains(query, ignoreCase = true)
            }
        }

        if (desc) items = items.sortedByDescending { it.timestamp }
        else items = items.sortedBy { it.timestamp }

        items
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun toggleSortOrder() {
        _sortDescending.value = !_sortDescending.value
    }

    fun deleteItem(item: TranslationEntity) {
        viewModelScope.launch { dao.deleteTranslation(item) }
    }

    fun deleteAll() {
        viewModelScope.launch { dao.deleteAllTranslations() }
    }
}