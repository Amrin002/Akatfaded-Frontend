package com.localclasstech.layanandesa.settings.utils

import android.util.Log
import java.net.URL

// Perbaikan URL Constant
object UrlConstant {
    private const val TAG = "UrlConstant"
    // Base URL untuk API
    const val BASE_URL = "http://192.168.56.1:8000/"

    // Base URL untuk gambar (mengarah ke direktori storage Laravel)
    const val IMAGE_BASE_URL = "${BASE_URL}storage/"

    // Daftar format gambar yang didukung
    private val SUPPORTED_FORMATS = listOf("jpg", "jpeg", "png", "webp")

    /**
     * Mengonversi path gambar relatif menjadi URL gambar yang valid
     * @param imagePath Path atau URL gambar
     * @param fallbackToDefault Apakah akan kembali ke gambar default jika gagal
     */
    fun getValidImageUrl(imagePath: String?, fallbackToDefault: Boolean = true): String {
        if (imagePath.isNullOrEmpty()) {
            Log.d(TAG, "Empty image path")
            return if (fallbackToDefault) getDefaultImageUrl() else ""
        }

        try {
            // Cek apakah sudah berupa URL lengkap
            if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                Log.d(TAG, "Valid URL format detected: $imagePath")
                return imagePath
            }

            // Bersihkan path dari karakter yang tidak perlu
            val cleanPath = imagePath.trim().removePrefix("/")

            // Cek format gambar
            val extension = cleanPath.substringAfterLast('.', "").lowercase()
            if (extension !in SUPPORTED_FORMATS) {
                Log.w(TAG, "Unsupported image format: $extension")
                return if (fallbackToDefault) getDefaultImageUrl() else ""
            }

            // Gabungkan dengan base URL
            val finalUrl = "$IMAGE_BASE_URL$cleanPath"
            Log.d(TAG, "Final image URL: $finalUrl")

            // Validate URL format
            URL(finalUrl) // Akan throw exception jika format URL invalid

            return finalUrl
        } catch (e: Exception) {
            Log.e(TAG, "Error creating valid image URL from $imagePath: ${e.message}")
            return if (fallbackToDefault) getDefaultImageUrl() else ""
        }
    }
    fun getValidImageUrlWithTimestamp(imagePath: String?, fallbackToDefault: Boolean = true): String {
        val baseUrl = getValidImageUrl(imagePath, fallbackToDefault)

        // Tambahkan timestamp untuk memastikan URL unik
        return if (baseUrl.isNotEmpty()) {
            "$baseUrl?t=${System.currentTimeMillis()}"
        } else {
            baseUrl
        }
    }
    /**
     * Mendapatkan URL gambar default jika gambar tidak tersedia
     */
    fun getDefaultImageUrl(): String {
        // Pastikan Anda memiliki gambar placeholder di drawable
        return "${BASE_URL}"
    }
}