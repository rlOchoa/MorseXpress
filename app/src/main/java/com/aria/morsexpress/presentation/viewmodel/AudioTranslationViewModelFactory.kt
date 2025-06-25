package com.aria.morsexpress.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aria.morsexpress.data.local.dao.TranslationDao

class AudioTranslationViewModelFactory(
    private val dao: TranslationDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AudioInputViewModel::class.java)) {
            return AudioInputViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
