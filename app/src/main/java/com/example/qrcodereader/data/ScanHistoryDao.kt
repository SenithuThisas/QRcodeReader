package com.example.qrcodereader.data

import androidx.room.*
import com.example.qrcodereader.model.ScanHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Insert
    suspend fun insert(item: ScanHistoryItem)

    @Delete
    suspend fun delete(item: ScanHistoryItem)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ScanHistoryItem>>

    @Query("SELECT * FROM scan_history WHERE content LIKE :query OR previewTitle LIKE :query ORDER BY timestamp DESC")
    fun search(query: String): Flow<List<ScanHistoryItem>>
}
