@Entity(tableName = "scan_history")
data class ScanHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isUrl: Boolean = content.isValidUrl(),
    val previewTitle: String? = null,
    val previewImage: String? = null
)

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

@Database(entities = [ScanHistoryItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
}