package com.example.qrcodereader.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileExport {

    fun exportHistoryToFile(
        context: Context,
        items: List<ScanHistoryItem>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "qr_history_${dateFormat.format(Date())}.csv"

            // Prepare CSV content
            val content = items.joinToString("\n") { item ->
                listOf(
                    item.timestamp,
                    if (item.isUrl) "URL" else "TEXT",
                    item.content,
                    item.previewTitle ?: ""
                ).joinToString(",") { field -> "\"${field.toString().replace("\"", "\"\"")}\"" }
            }

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/csv"
                putExtra(Intent.EXTRA_TITLE, fileName)
            }

            // Start the activity and handle the result in onActivityResult (or ActivityResult API)
            if (context is Activity) {
                (context as Activity).startActivityForResult(intent, EXPORT_REQUEST_CODE)
            } else {
                onError("Context is not an Activity")
            }

            // This should be handled in the onActivityResult or ActivityResult API
            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Unknown error")
        }
    }

    // Handle the result of the file creation intent
    fun handleFileResult(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        content: String,
        onError: (String) -> Unit
    ) {
        if (requestCode == EXPORT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let { fileUri ->
                try {
                    val outputStream: OutputStream? = context.contentResolver.openOutputStream(fileUri)
                    outputStream?.use {
                        it.write(content.toByteArray())
                        it.flush()
                    }
                } catch (e: Exception) {
                    onError("Failed to write to file: ${e.message}")
                }
            } ?: onError("Invalid file URI")
        }
    }

    companion object {
        private const val EXPORT_REQUEST_CODE = 1
    }
}
