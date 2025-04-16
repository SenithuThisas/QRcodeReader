package com.example.qrcodereader.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isUrl: Boolean = content.isValidUrl(),
    val previewTitle: String? = null,
    val previewImage: String? = null
)
