package com.aria.morsexpress.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inputType: String,
    val inputPathOrContent: String,
    val morseCode: String,
    val translatedText: String,
    val timestamp: Long
)
