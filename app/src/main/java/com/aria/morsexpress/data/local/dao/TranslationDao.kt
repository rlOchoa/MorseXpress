package com.aria.morsexpress.data.local.dao

import androidx.room.*
import com.aria.morsexpress.data.local.entity.TranslationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translations ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TranslationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(translation: TranslationEntity)

    @Delete
    suspend fun delete(translation: TranslationEntity)
}
