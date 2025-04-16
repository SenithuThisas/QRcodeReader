package com.example.qrcodereader.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.qrcodereader.model.ScanHistoryItem
import com.example.qrcodereader.data.ScanHistoryDao

@Database(entities = [ScanHistoryItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
}
