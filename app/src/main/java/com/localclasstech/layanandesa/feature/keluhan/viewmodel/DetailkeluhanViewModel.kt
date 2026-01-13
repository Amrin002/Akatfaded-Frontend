package com.localclasstech.layanandesa.feature.keluhan.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.keluhan.data.Keluhan
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.keluhan.data.repository.KeluhanRepository
import com.localclasstech.layanandesa.settings.utils.ImageCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class DetailkeluhanViewModel(private val repository: KeluhanRepository) : ViewModel() {
    private val _keluhanDetail = MutableLiveData<Keluhan>()
    val keluhanDetail: LiveData<Keluhan> get() = _keluhanDetail

    private val _operationResult = MutableLiveData<Boolean?>()
    val operationResult: LiveData<Boolean?> get() = _operationResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**
     * Mengambil detail keluhan berdasarkan ID
     */
    fun getKeluhanDetail(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getDetailKeluhanById(id)
                if (response.isSuccessful) {
                    _keluhanDetail.postValue(response.body()?.data)
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Keluhan tidak ditemukan"
                        401 -> "Tidak memiliki akses, silakan login ulang"
                        403 -> "Akses ditolak"
                        500 -> "Terjadi kesalahan pada server"
                        else -> "Gagal mengambil detail keluhan (${response.code()})"
                    }
                    _error.postValue(errorMessage)
                }
            } catch (e: Exception) {
                _error.postValue(handleException(e))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * BARU: Membuat keluhan dengan kompresi gambar otomatis
     * @param context Context untuk akses file system
     * @param judul Judul keluhan
     * @param isi Isi/deskripsi keluhan
     * @param imageUri Uri gambar yang akan diupload (nullable)
     */
    fun createKeluhanWithImage(
        context: Context,
        judul: String,
        isi: String,
        imageUri: Uri?
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // 1. Proses gambar jika ada
                val imagePart = if (imageUri != null) {
                    processImage(context, imageUri)
                } else {
                    null
                }

                // 2. Buat request
                val request = KeluhanRequest(
                    judul = judul,
                    isi = isi,
                    gambar = imagePart
                )

                // 3. Kirim ke repository
                val response = repository.createKeluhan(request)

                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Data keluhan tidak valid"
                        401 -> "Tidak memiliki akses, silakan login ulang"
                        413 -> "File gambar terlalu besar (max 2MB)"
                        422 -> "Data tidak sesuai format yang diperlukan"
                        500 -> "Terjadi kesalahan pada server"
                        else -> "Gagal membuat keluhan (${response.code()})"
                    }
                    _error.postValue(errorMessage)
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue(handleException(e))
                _operationResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Mengirimkan keluhan baru (versi lama - backward compatibility)
     */
    fun createKeluhan(request: KeluhanRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createKeluhan(request)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Data keluhan tidak valid"
                        401 -> "Tidak memiliki akses, silakan login ulang"
                        413 -> "File gambar terlalu besar"
                        422 -> "Data tidak sesuai format yang diperlukan"
                        500 -> "Terjadi kesalahan pada server"
                        else -> "Gagal membuat keluhan (${response.code()})"
                    }
                    _error.postValue(errorMessage)
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue(handleException(e))
                _operationResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * BARU: Update keluhan dengan kompresi gambar otomatis
     * @param context Context untuk akses file system
     * @param id ID keluhan yang akan diupdate
     * @param judul Judul keluhan baru
     * @param isi Isi/deskripsi keluhan baru
     * @param imageUri Uri gambar baru (nullable, jika null berarti tidak ganti gambar)
     */
    fun updateKeluhanWithImage(
        context: Context,
        id: Int,
        judul: String,
        isi: String,
        imageUri: Uri?
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // 1. Proses gambar jika ada
                val imagePart = if (imageUri != null) {
                    processImage(context, imageUri)
                } else {
                    null
                }

                // 2. Buat request
                val request = KeluhanRequest(
                    judul = judul,
                    isi = isi,
                    gambar = imagePart
                )

                // 3. Update ke repository
                val response = repository.updateKeluhan(id, request)

                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                    // Refresh detail keluhan setelah update berhasil
                    response.body()?.data?.let {
                        _keluhanDetail.postValue(it)
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Data keluhan tidak valid"
                        401 -> "Tidak memiliki akses, silakan login ulang"
                        403 -> "Tidak memiliki izin untuk mengubah keluhan ini"
                        404 -> "Keluhan tidak ditemukan"
                        413 -> "File gambar terlalu besar (max 2MB)"
                        422 -> "Data tidak sesuai format yang diperlukan"
                        500 -> "Terjadi kesalahan pada server. Silakan coba lagi"
                        else -> "Gagal memperbarui keluhan (${response.code()})"
                    }
                    _error.postValue(errorMessage)
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue(handleException(e))
                _operationResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Memperbarui keluhan (versi lama - backward compatibility)
     */
    fun updateKeluhan(id: Int, request: KeluhanRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateKeluhan(id, request)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                    // Refresh detail keluhan setelah update berhasil
                    response.body()?.data?.let {
                        _keluhanDetail.postValue(it)
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Data keluhan tidak valid"
                        401 -> "Tidak memiliki akses, silakan login ulang"
                        403 -> "Tidak memiliki izin untuk mengubah keluhan ini"
                        404 -> "Keluhan tidak ditemukan"
                        413 -> "File gambar terlalu besar"
                        422 -> "Data tidak sesuai format yang diperlukan"
                        500 -> "Terjadi kesalahan pada server. Silakan coba lagi"
                        else -> "Gagal memperbarui keluhan (${response.code()})"
                    }
                    _error.postValue(errorMessage)
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue(handleException(e))
                _operationResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Menghapus keluhan berdasarkan ID
     */
    fun deleteKeluhan(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteKeluhan(id)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Tidak memiliki akses, silakan login ulang"
                        403 -> "Tidak memiliki izin untuk menghapus keluhan ini"
                        404 -> "Keluhan tidak ditemukan"
                        500 -> "Terjadi kesalahan pada server"
                        else -> "Gagal menghapus keluhan (${response.code()})"
                    }
                    _error.postValue(errorMessage)
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue(handleException(e))
                _operationResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * BARU: Proses gambar dengan kompresi
     * @return MultipartBody.Part atau null jika gagal
     */
    private suspend fun processImage(context: Context, imageUri: Uri): MultipartBody.Part? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Kompresi gambar
                val compressedFile = ImageCompressor.compressImage(
                    context = context,
                    imageUri = imageUri,
                    maxSizeKB = 1536 // 1.5 MB untuk safety margin (backend max 2MB)
                )

                if (compressedFile == null) {
                    _error.postValue("Gagal memproses gambar. Silakan coba gambar lain.")
                    return@withContext null
                }

                // 2. Validasi ukuran final
                if (!ImageCompressor.isFileSizeValid(compressedFile, maxSizeMB = 2)) {
                    _error.postValue("Ukuran gambar terlalu besar (max 2MB)")
                    compressedFile.delete()
                    return@withContext null
                }

                // 3. Convert ke MultipartBody.Part
                val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "gambar",
                    "keluhan_${System.currentTimeMillis()}.jpg",
                    requestFile
                )

                imagePart
            } catch (e: Exception) {
                _error.postValue("Gagal memproses gambar: ${e.message}")
                null
            }
        }
    }

    /**
     * Handle berbagai jenis exception dengan pesan yang lebih informatif
     */
    private fun handleException(e: Exception): String {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    401 -> "Sesi telah berakhir, silakan login ulang"
                    403 -> "Akses ditolak"
                    404 -> "Data tidak ditemukan"
                    500 -> "Terjadi kesalahan pada server"
                    else -> "Terjadi kesalahan: ${e.message()}"
                }
            }
            is SocketTimeoutException -> "Koneksi timeout, silakan coba lagi"
            is IOException -> "Periksa koneksi internet Anda"
            else -> "Terjadi kesalahan: ${e.message ?: "Unknown error"}"
        }
    }

    /**
     * Reset operation result untuk mencegah trigger berulang
     */
    fun resetOperationResult() {
        _operationResult.value = null
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
}