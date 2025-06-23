package com.aria.morsexpress.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aria.morsexpress.data.local.database.AppDatabase
import com.aria.morsexpress.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.Companion.getInstance(application).historyDao()

    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow<String?>(null)
    private val _reverseOrder = MutableStateFlow(false)

    val history = combine(
        _searchQuery, _filterType, _reverseOrder
    ) { query, type, reverse ->
        Triple(query, type, reverse)
    }.flatMapLatest { (query, type, reverse) ->
        val baseFlow = when {
            query.isNotBlank() -> dao.searchHistory(query)
            type != null -> dao.getHistoryByType(type)
            else -> dao.getAllHistory()
        }

        baseFlow.map { list -> if (reverse) list.reversed() else list }
    }.stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())

    fun setQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: String?) {
        _filterType.value = type
    }

    fun setOrderReversed(reversed: Boolean) {
        _reverseOrder.value = reversed
    }

    fun deleteItem(item: HistoryEntity) {
        viewModelScope.launch { dao.deleteHistory(item) }
    }

    fun deleteAll() {
        viewModelScope.launch { dao.deleteAll() }
    }

    fun insert(item: HistoryEntity) {
        viewModelScope.launch { dao.insertHistory(item) }
    }
}