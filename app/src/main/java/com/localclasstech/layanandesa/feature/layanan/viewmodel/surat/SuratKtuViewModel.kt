package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu.CreateSuratKtuRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu.SuratKtuResponse
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtuRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class SuratKtuViewModel(private val repository: SuratKtuRepository) : ViewModel() {
    private val _detailSuratKtu = MutableLiveData<SuratKtuResponse>()
    val detailSuratKtu: LiveData<SuratKtuResponse> get() = _detailSuratKtu

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> get() = _deleteResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _pdfDownloadResult = MutableLiveData<Pair<Boolean, ResponseBody?>>()
    val pdfDownloadResult: LiveData<Pair<Boolean, ResponseBody?>> get() = _pdfDownloadResult

    // Fetch Surat KTU Detail
    fun fetchSuratKtuDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getDetailSuratKtuById(id)
                if (response.isSuccessful && response.body() != null) {
                    _detailSuratKtu.postValue(response.body()?.data)
                } else {
                    _error.postValue("Gagal memuat detail surat: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                Log.e("SuratKtuViewModel", "Exception: ${e.message}")
            }
            finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Create Surat KTU
    fun createSuratKtu(suratKtuRequest: CreateSuratKtuRequest) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.createSuratKtu(suratKtuRequest)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal membuat surat: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _operationResult.postValue(false)
                Log.e("SuratKtuViewModel", "Exception: ${e.message}")
            }
            finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Update Surat KTU
    fun updateSuratKtu(id: Int, suratKtuRequest: CreateSuratKtuRequest) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {

                val response = repository.updateSuratKtu(id, suratKtuRequest)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal memperbarui surat: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _operationResult.postValue(false)
                Log.e("SuratKtuViewModel", "Exception: ${e.message}")
            }
            finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Delete Surat KTU
    fun deleteSuratKtu(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteSuratKtu(id)
                if (response.isSuccessful) {
                    _deleteResult.postValue(true)
                } else {
                    _error.postValue("Gagal menghapus surat: ${response.message()}")
                    _deleteResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _deleteResult.postValue(false)
                Log.e("SuratKtuViewModel", "Exception during delete: ${e.message}")
            }
        }
    }

    // Get Download URL for Surat KTU
    private val _downloadUrlResult = MutableLiveData<DownloadUrlResult>()
    val downloadUrlResult: LiveData<DownloadUrlResult> = _downloadUrlResult

    fun getDownloadUrl(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getDownloadUrlSuratKtu(id)

                if (response.isSuccessful && response.body() != null) {
                    val downloadUrl = response.body()?.download_url
                    _downloadUrlResult.postValue(DownloadUrlResult(true, downloadUrl))
                } else {
                    _error.postValue("Gagal mendapatkan URL download: ${response.message()}")
                    _downloadUrlResult.postValue(DownloadUrlResult(false, null))
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _downloadUrlResult.postValue(DownloadUrlResult(false, null))
            }
            finally {
                _isLoading.postValue(false)
            }
        }
    }

    data class DownloadUrlResult(val success: Boolean, val downloadUrl: String?)

    // Export PDF for Surat KTU
    fun exportPdfSuratKtu(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.exportPdfSuratKtu(id)

                if (response.isSuccessful && response.body() != null) {
                    _pdfDownloadResult.postValue(Pair(true, response.body()))
                } else {
                    _error.postValue("Gagal mengunduh PDF: ${response.message()}")
                    _pdfDownloadResult.postValue(Pair(false, null))
                    Log.e("SuratKtuViewModel", "PDF export failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _pdfDownloadResult.postValue(Pair(false, null))
                Log.e("SuratKtuViewModel", "Exception during PDF export: ${e.message}")
            }
        }
    }
}
