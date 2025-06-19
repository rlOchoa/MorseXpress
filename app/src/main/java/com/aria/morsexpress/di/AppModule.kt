package com.aria.morsexpress.di

import android.app.Application
import androidx.room.Room
import com.aria.morsexpress.data.local.dao.TranslationDao
import com.aria.morsexpress.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "morsexpress_db"
        ).build()

    @Provides
    fun provideTranslationDao(db: AppDatabase): TranslationDao =
        db.translationDao()
}