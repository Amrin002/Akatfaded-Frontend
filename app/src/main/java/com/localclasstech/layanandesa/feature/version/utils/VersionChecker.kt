package com.localclasstech.layanandesa.feature.version.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AppCompatActivity
import com.localclasstech.layanandesa.BuildConfig
import com.localclasstech.layanandesa.feature.version.data.VersionCheckRequest
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.utils.UrlConstant
import kotlinx.coroutines.launch

class VersionChecker(private val context: Context) {

    companion object {
        private const val TAG = "VersionChecker"
    }

    /**
     * Cek version aplikasi dan tampilkan dialog update jika perlu
     **/

    fun checkForUpdates() {
        if (context !is AppCompatActivity) {
            Log.e(TAG, "Context must be AppCompatActivity")
            return
        }

        context.lifecycleScope.launch {
            try {
                Log.d(TAG, "Checking for app updates...")

                // Gunakan endpoint yang sama dengan Postman
                val response = RetrofitClient.versionApiService.getLatestVersion("android")

                if (response.isSuccessful) {
                    Log.d(TAG, "Raw response body: ${response.body()}")

                    val versionData = response.body()?.data

                    if (versionData != null) {
                        // Cek apakah perlu update berdasarkan version code
                        val currentVersionCode = BuildConfig.VERSION_CODE
                        val latestVersionCode = versionData.latestVersionCode

                        val needsUpdate = currentVersionCode < latestVersionCode

                        if (needsUpdate) {
                            Log.d(TAG, "Update available: ${versionData.latestVersion}")
                            Log.d(TAG, "is_force_update from API: ${versionData.isForceUpdate}")
                            showUpdateDialog(versionData)
                        } else {
                            Log.d(TAG, "App is up to date")
                        }
                    } else {
                        Log.d(TAG, "No version data received")
                    }

                } else {
                    Log.e(TAG, "Failed to check version: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error checking version", e)
            }
        }
    }

    /**
     * Cek version aplikasi secara manual dengan feedback
     * Untuk keperluan manual check dari menu atau tombol
     */
    fun checkForUpdatesManually() {
        if (context !is AppCompatActivity) {
            Log.e(TAG, "Context must be AppCompatActivity")
            return
        }

        context.lifecycleScope.launch {
            try {
                Log.d(TAG, "Manual checking for app updates...")

                // Gunakan endpoint yang sama dengan auto check
                val response = RetrofitClient.versionApiService.getLatestVersion("android")

                if (response.isSuccessful) {
                    Log.d(TAG, "Manual check response: ${response.body()}")

                    val versionData = response.body()?.data

                    if (versionData != null) {
                        // Cek apakah perlu update berdasarkan version code
                        val currentVersionCode = BuildConfig.VERSION_CODE
                        val latestVersionCode = versionData.latestVersionCode

                        val needsUpdate = currentVersionCode < latestVersionCode

                        if (needsUpdate) {
                            Log.d(TAG, "Update available: ${versionData.latestVersion}")
                            showUpdateDialog(versionData)
                        } else {
                            Log.d(TAG, "App is up to date - showing up to date message")
                            showUpToDateMessage()
                        }
                    } else {
                        Log.d(TAG, "No version data received")
                        showErrorMessage()
                    }

                } else {
                    Log.e(TAG, "Failed to check version: ${response.code()}")
                    showErrorMessage()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error checking version", e)
                showErrorMessage()
            }
        }
    }

    /**
     * Tampilkan dialog update dengan info changelog dan tombol download
     */
    private fun showUpdateDialog(versionData: com.localclasstech.layanandesa.feature.version.data.VersionData) {
        val activity = context as AppCompatActivity

        // Build changelog text
        val changelogText = if (versionData.changelog.isNotEmpty()) {
            versionData.changelog.joinToString("\n") { "• $it" }
        } else {
            "Perbaikan dan peningkatan performa"
        }

        val title = if (versionData.isForceUpdate) {
            "Update Wajib Tersedia"
        } else {
            "Update Tersedia"
        }

        val message = """
            Versi baru aplikasi DESAKU telah tersedia!
            
            Versi Saat Ini: ${versionData.currentVersion}
            Versi Terbaru: ${versionData.latestVersion}
            Ukuran File: ${versionData.fileSize}
            Tanggal Rilis: ${versionData.releaseDate}
            
            Yang Baru:
            $changelogText
        """.trimIndent()

        val builder = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(!versionData.isForceUpdate) // Force update tidak bisa di-cancel

        // Tombol Download
        builder.setPositiveButton("Download Sekarang") { _, _ ->
            downloadUpdate(versionData.downloadUrl)
        }

        // Tombol Nanti (hanya jika bukan force update)
        if (!versionData.isForceUpdate) {
            builder.setNegativeButton("Nanti") { dialog, _ ->
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Download APK menggunakan method yang sama seperti download PDF surat
     * Langsung buka browser untuk download
     */
    private fun downloadUpdate(downloadUrl: String) {
        try {
            // Pastikan URL lengkap
            val fullUrl = if (downloadUrl.startsWith("http://") || downloadUrl.startsWith("https://")) {
                downloadUrl
            } else {
                "${UrlConstant.BASE_URL}storage/$downloadUrl"
            }

            Log.d(TAG, "Opening download URL in browser: $fullUrl")

            // Method yang sama seperti downloadPdf di SuratKtmFragment
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
            browserIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            context.startActivity(browserIntent)

            Log.d(TAG, "Successfully opened browser for APK download")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to open download URL", e)
            showDownloadError(downloadUrl)
        }
    }

    /**
     * Tampilkan dialog error download dengan opsi copy URL
     */
    private fun showDownloadError(downloadUrl: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Download URL", downloadUrl)
            clipboard.setPrimaryClip(clip)

            AlertDialog.Builder(context)
                .setTitle("Tidak Dapat Membuka Browser")
                .setMessage("Tidak dapat membuka browser untuk download. URL download telah disalin ke clipboard:\n\n$downloadUrl\n\nSilakan buka browser secara manual dan paste URL tersebut.")
                .setPositiveButton("OK", null)
                .show()
        } catch (clipboardError: Exception) {
            Log.e(TAG, "Failed to copy to clipboard", clipboardError)

            AlertDialog.Builder(context)
                .setTitle("Error Download")
                .setMessage("Tidak dapat membuka browser untuk download. Silakan download manual dari:\n\n$downloadUrl")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    /**
     * Tampilkan pesan bahwa aplikasi sudah update
     */
    private fun showUpToDateMessage() {
        val activity = context as AppCompatActivity

        val currentVersion = BuildConfig.VERSION_NAME
        val currentVersionCode = BuildConfig.VERSION_CODE

        val message = """
            Aplikasi DESAKU Anda sudah menggunakan versi terbaru!
            
            Versi Saat Ini: $currentVersion
            Version Code: $currentVersionCode
            
            Anda tidak perlu melakukan update saat ini.
            Tetap pantau update terbaru untuk fitur dan perbaikan baru.
        """.trimIndent()

        AlertDialog.Builder(activity)
            .setTitle("✓ Aplikasi Terbaru")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Cek Lagi") { _, _ ->
                checkForUpdatesManually()
            }
            .show()
    }

    /**
     * Tampilkan pesan error saat gagal mengecek update
     */
    private fun showErrorMessage() {
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage("Gagal mengecek update. Pastikan koneksi internet Anda stabil dan coba lagi.")
            .setPositiveButton("OK", null)
            .show()
    }
}
