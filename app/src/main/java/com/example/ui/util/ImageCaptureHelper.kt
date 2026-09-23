package com.example.ui.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object ImageCaptureHelper {
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, prefix: String = "member_photo"): String {
        return try {
            val directory = File(context.filesDir, "profiles")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, "${prefix}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            ""
        }
    }
}
