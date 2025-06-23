package com.aria.morsexpress.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aria.morsexpress.data.local.entity.TranslationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(translation: TranslationEntity)

    @Delete
    suspend fun deleteTranslation(translation: TranslationEntity)

    @Query("DELETE FROM translations")
    suspend fun deleteAllTranslations()

    @Query("SELECT * FROM translations ORDER BY timestamp DESC")
    fun getAllTranslations(): Flow<List<TranslationEntity>>

    @Query("SELECT * FROM translations WHERE inputType = :type ORDER BY timestamp ASC")
    fun getTranslationsByType(type: String): Flow<List<TranslationEntity>>

    @Query(
        """
        SELECT * FROM translations 
        WHERE originalText LIKE '%' || :query || '%' OR translatedText LIKE '%' || :query || '%' 
        ORDER BY timestamp ASC
    """
    )
    fun searchTranslations(query: String): Flow<List<TranslationEntity>>
}
