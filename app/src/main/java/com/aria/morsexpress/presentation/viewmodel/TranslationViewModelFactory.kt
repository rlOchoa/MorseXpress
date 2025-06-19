package com.aria.morsexpress.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aria.morsexpress.data.local.dao.TranslationDao

class TranslationViewModelFactory(
    private val translationDao: TranslationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TranslationViewModel::class.java)) {
            return TranslationViewModel(translationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
