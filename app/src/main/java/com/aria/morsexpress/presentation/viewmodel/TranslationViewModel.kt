package com.aria.morsexpress.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aria.morsexpress.data.local.dao.TranslationDao
import com.aria.morsexpress.data.local.entity.TranslationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TranslationViewModel(
    private val dao: TranslationDao
) : ViewModel() {

    val allTranslations: StateFlow<List<TranslationEntity>> =
        dao.getAllTranslations()
            .map { it.sortedByDescending { entry -> entry.timestamp } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertTranslation(
        originalText: String,
        translatedText: String,
        inputType: String,
        inputPathOrContent: String,
        morseCode: String
    ) {
        val newEntry = TranslationEntity(
            originalText = originalText,
            translatedText = translatedText,
            inputType = inputType,
            timestamp = System.currentTimeMillis(),
            inputPathOrContent = inputPathOrContent,
            morseCode = morseCode
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(newEntry)
        }
    }

    fun deleteTranslation(translation: TranslationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTranslation(translation)
        }
    }
}