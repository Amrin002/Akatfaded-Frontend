package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.network.data.DownloadUrlResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.SuratDomisiliResponse
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class SuratDomisiliViewModel(private val repository: SuratDomisiliRepository) : ViewModel() {
    private val _detailSuratDomisili = MutableLiveData<SuratDomisiliResponse>()
    val detailSuratDomisili: LiveData<SuratDomisiliResponse> get() = _detailSuratDomisili

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> get() = _deleteResult

    private val _pdfDownloadResult = MutableLiveData<Pair<Boolean, ResponseBody?>>()
    val pdfDownloadResult: LiveData<Pair<Boolean, ResponseBody?>> get() = _pdfDownloadResult

    private val _downloadUrlResult = MutableLiveData<DownloadUrlResult>()
    val downloadUrlResult: LiveData<DownloadUrlResult> = _downloadUrlResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    data class DownloadUrlResult(val success: Boolean, val downloadUrl: String?)

    fun fetchSuratDomisiliDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getDetailSuratDomisiliById(id)
                if (response.isSuccessful && response.body() != null) {
                    _detailSuratDomisili.postValue(response.body()?.data)
                } else {
                    _error.postValue("Gagal memuat detail surat: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                Log.e("SuratDomisiliViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun createSuratDomisili(request: CreateSuratDomisiliRequest) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.createSuratDomisili(request)
                if (response.isSuccessful && response.body() != null) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal membuat surat: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Gagal membuat surat: ${e.message}")
                _operationResult.postValue(false)
                Log.e("SuratDomisiliViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateSuratDomisili(id: Int, request: CreateSuratDomisiliRequest) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.updateSuratDomisili(id, request)
                if (response.isSuccessful && response.body() != null) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal memperbarui surat: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Gagal memperbarui surat: ${e.message}")
                _operationResult.postValue(false)
                Log.e("SuratDomisiliViewModel", "Error message: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteSuratDomisili(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.deleteSuratDomisili(id)
                if (response.isSuccessful) {
                    _deleteResult.postValue(true)
                } else {
                    _error.postValue("Gagal menghapus surat: ${response.message()}")
                    _deleteResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _deleteResult.postValue(false)
                Log.e("SuratDomisiliViewModel", "Exception during delete: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getDownloadUrl(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getDownloadUrl(id)

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
                Log.e("SuratDomisiliViewModel", "Exception: ${e.message}")
            }
            finally {
                _isLoading.postValue(false)
            }

        }
    }

//    fun exportPdfSuratDomisili(id: Int) {
//        viewModelScope.launch {
//            try {
//                val response = repository.exportPdfSuratDomisili(id)
//
//                if (response.isSuccessful && response.body() != null) {
//                    _pdfDownloadResult.postValue(Pair(true, response.body()))
//                } else {
//                    _error.postValue("Gagal mengunduh PDF: ${response.message()}")
//                    _pdfDownloadResult.postValue(Pair(false, null))
//                    Log.e("SuratDomisiliViewModel", "PDF export failed: ${response.message()}")
//                }
//            } catch (e: Exception) {
//                _error.postValue("Error: ${e.message}")
//                _pdfDownloadResult.postValue(Pair(false, null))
//                Log.e("SuratDomisiliViewModel", "Exception during PDF export: ${e.message}")
//            }
//        }
//    }
}