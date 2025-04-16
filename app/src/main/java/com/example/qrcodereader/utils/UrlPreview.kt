package com.example.qrcodereader.utils

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

data class UrlPreview(
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val domain: String?
)

suspend fun fetchUrlPreview(url: String): UrlPreview? = withContext(Dispatchers.IO) {
    return@withContext try {
        val doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0")
            .timeout(10000)
            .get()

        val title = doc.select("meta[property=og:title]").attr("content")
            .takeIf { it.isNotBlank() }
            ?: doc.title()

        val description = doc.select("meta[property=og:description]").attr("content")
            .takeIf { it.isNotBlank() }
            ?: doc.select("meta[name=description]").attr("content")

        val imageUrl = doc.select("meta[property=og:image]").attr("content")
            .takeIf { it.isNotBlank() }

        val domain = Uri.parse(url).host?.removePrefix("www.")

        UrlPreview(
            title = title,
            description = description,
            imageUrl = imageUrl,
            domain = domain
        )
    } catch (e: Exception) {
        null
    }
}
