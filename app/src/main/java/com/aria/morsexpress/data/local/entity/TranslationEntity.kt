package com.aria.morsexpress.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalText: String,
    val translatedText: String,
    val inputType: String,
    val timestamp: Long,
    val inputPathOrContent: String,
    val morseCode: String
)
