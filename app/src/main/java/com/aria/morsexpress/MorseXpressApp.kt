package com.aria.morsexpress

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MorseXpressApp : Application() {

    companion object {
        lateinit var prefs: SharedPreferences
        var isDarkTheme = mutableStateOf(false)
    }

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("morsexpress_prefs", Context.MODE_PRIVATE)
        isDarkTheme.value = prefs.getBoolean("dark_theme", false)
    }

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
        prefs.edit().putBoolean("dark_theme", isDarkTheme.value).apply()
    }
}
