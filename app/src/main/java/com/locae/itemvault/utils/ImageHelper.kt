package com.locae.itemvault.utils

import android.content.Context
import android.net.Uri
import java.io.File

object ImageHelper {

    // Saves a URI (from gallery or camera) into app's internal storage and returns the saved file path
    fun saveImageToInternal(context: Context, uri: Uri): String {
        val file = File(context.filesDir, "img_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }

    // Converts a comma-separated string of paths into a List
    fun pathsToList(paths: String): List<String> {
        return if (paths.isEmpty()) emptyList() else paths.split(",")
    }

    // Converts a List of paths back to a comma-separated string
    fun listToPaths(list: List<String>): String {
        return list.joinToString(",")
    }
}
