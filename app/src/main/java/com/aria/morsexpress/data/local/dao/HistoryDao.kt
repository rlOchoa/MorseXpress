package com.aria.morsexpress.data.local.dao

import androidx.room.*
import com.aria.morsexpress.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entry: HistoryEntity)

    @Delete
    suspend fun deleteHistory(entry: HistoryEntity)

    @Query("DELETE FROM translation_history")
    suspend fun deleteAll()

    @Query("SELECT * FROM translation_history ORDER BY timestamp ASC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM translation_history WHERE inputType = :type ORDER BY timestamp ASC")
    fun getHistoryByType(type: String): Flow<List<HistoryEntity>>

    @Query("""
        SELECT * FROM translation_history 
        WHERE originalText LIKE '%' || :query || '%' OR translatedText LIKE '%' || :query || '%' 
        ORDER BY timestamp ASC
    """)
    fun searchHistory(query: String): Flow<List<HistoryEntity>>
}
