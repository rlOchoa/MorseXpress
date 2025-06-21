package com.aria.morsexpress.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aria.morsexpress.data.local.dao.TranslationDao

class TranslationViewModelFactory(
    private val dao: TranslationDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TranslationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TranslationViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}