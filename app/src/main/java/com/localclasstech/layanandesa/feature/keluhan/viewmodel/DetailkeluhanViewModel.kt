package com.localclasstech.layanandesa.feature.keluhan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.keluhan.data.Keluhan
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.keluhan.data.repository.KeluhanRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class DetailkeluhanViewModel(private val repository: KeluhanRepository) : ViewModel() {
    private val _keluhanDetail = MutableLiveData<Keluhan>()
    val keluhanDetail: LiveData<Keluhan> get() = _keluhanDetail

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

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
     * Mengirimkan keluhan baru (termasuk gambar)
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
     * Memperbarui keluhan (termasuk gambar)
     */
    fun updateKeluhan(id: Int, request: KeluhanRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateKeluhan(id, request)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                    // Refresh detail keluhan setelah update berhasil
                    getKeluhanDetail(id)
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