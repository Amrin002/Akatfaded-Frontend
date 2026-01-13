// File: ImageCompressor.kt
package com.localclasstech.layanandesa.settings.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageCompressor {
    private const val TAG = "ImageCompressor"

    /**
     * Kompresi gambar dengan target max size untuk backend yang limit 2MB
     * @param context Context aplikasi
     * @param imageUri Uri gambar yang akan dikompres
     * @param maxSizeKB Target maksimal ukuran file dalam KB (default 1536 KB = 1.5 MB)
     * @return File gambar yang sudah dikompres, atau null jika gagal
     */
    fun compressImage(
        context: Context,
        imageUri: Uri,
        maxSizeKB: Int = 1536 // 1.5 MB untuk safety margin
    ): File? {
        return try {
            Log.d(TAG, "Starting compression for: $imageUri")

            // 1. Decode gambar dari Uri
            val inputStream = context.contentResolver.openInputStream(imageUri)
            var originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from uri")
                return null
            }

            Log.d(TAG, "Original size: ${originalBitmap.width}x${originalBitmap.height}")

            // 2. Fix orientasi gambar (penting untuk foto dari kamera)
            originalBitmap = fixOrientation(context, imageUri, originalBitmap)

            // 3. Resize gambar jika terlalu besar (max 1920x1920 untuk kualitas bagus)
            val resizedBitmap = resizeBitmap(originalBitmap, 1920)

            // 4. Kompresi dengan quality bertahap sampai di bawah maxSizeKB
            val compressedFile = File(
                context.cacheDir,
                "compressed_${System.currentTimeMillis()}.jpg"
            )

            var quality = 95 // Mulai dari quality tinggi
            var attempt = 0

            do {
                attempt++
                FileOutputStream(compressedFile).use { outputStream ->
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                }

                val fileSizeKB = compressedFile.length() / 1024
                Log.d(TAG, "Attempt $attempt: Quality=$quality, Size=${fileSizeKB}KB")

                quality -= 5 // Turunkan quality 5% setiap percobaan

            } while (compressedFile.length() > maxSizeKB * 1024 && quality > 20)

            // 5. Validasi hasil akhir
            val finalSizeKB = compressedFile.length() / 1024
            val finalSizeMB = finalSizeKB / 1024f

            if (compressedFile.length() > 2048 * 1024) {
                Log.e(TAG, "Compressed file still too large: ${finalSizeMB}MB")
                compressedFile.delete()
                return null
            }

            Log.d(TAG, "Compression successful: ${finalSizeMB}MB (${finalSizeKB}KB)")

            // Cleanup
            if (resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }
            originalBitmap.recycle()

            return compressedFile

        } catch (e: Exception) {
            Log.e(TAG, "Error compressing image: ${e.message}", e)
            null
        }
    }

    /**
     * Fix orientasi gambar berdasarkan EXIF data
     * Penting untuk foto yang diambil dari kamera
     */
    private fun fixOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = inputStream?.let { ExifInterface(it) }
            inputStream?.close()

            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: IOException) {
            Log.w(TAG, "Failed to read EXIF orientation: ${e.message}")
            bitmap
        }
    }

    /**
     * Rotate bitmap dengan angle tertentu
     */
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Resize bitmap dengan mempertahankan aspect ratio
     */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Jika sudah cukup kecil, tidak perlu resize
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Mendapatkan ukuran file dalam format readable
     */
    fun getReadableFileSize(file: File): String {
        val sizeInBytes = file.length()
        return when {
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024} KB"
            else -> String.format("%.2f MB", sizeInBytes / (1024f * 1024f))
        }
    }

    /**
     * Validasi ukuran file sebelum upload
     */
    fun isFileSizeValid(file: File, maxSizeMB: Int = 2): Boolean {
        val sizeInMB = file.length() / (1024f * 1024f)
        return sizeInMB <= maxSizeMB
    }
}