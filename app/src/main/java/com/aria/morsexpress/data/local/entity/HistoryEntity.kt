package com.aria.morsexpress.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalText: String,
    val translatedText: String,
    val inputType: String, // "text", "image", "audio"
    val timestamp: Long = System.currentTimeMillis()
)