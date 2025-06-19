package com.aria.morsexpress.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aria.morsexpress.data.local.dao.TranslationDao
import com.aria.morsexpress.data.local.entity.TranslationEntity

@Database(entities = [TranslationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "morsexpress_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
