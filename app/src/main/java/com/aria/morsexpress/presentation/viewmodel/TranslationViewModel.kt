package com.aria.morsexpress.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aria.morsexpress.data.local.dao.TranslationDao
import com.aria.morsexpress.data.local.entity.TranslationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val dao: TranslationDao
) : ViewModel() {

    fun saveTranslation(input: String, output: String, inputType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(
                TranslationEntity(
                    inputType = inputType,
                    inputPathOrContent = input,
                    morseCode = if (inputType == "Texto") output else input,
                    translatedText = if (inputType == "Texto") input else output,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}