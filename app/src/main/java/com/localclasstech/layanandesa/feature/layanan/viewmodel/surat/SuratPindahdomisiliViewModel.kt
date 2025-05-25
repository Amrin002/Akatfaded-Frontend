package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili.CreateSuratPindahRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili.SuratPindahResponse
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratPindahRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class SuratPindahdomisiliViewModel(private val repository: SuratPindahRepository) : ViewModel() {

    private val _detailSuratPindah = MutableLiveData<SuratPindahResponse>()
    val detailSuratPindah: LiveData<SuratPindahResponse> get() = _detailSuratPindah

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

    private val _downloadUrlResult = MutableLiveData<DownloadUrlResult>()
    val downloadUrlResult: LiveData<DownloadUrlResult> = _downloadUrlResult

    data class DownloadUrlResult(val success: Boolean, val downloadUrl: String?)

    fun fetchSuratPindahDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getDetailSuratPindahById(id)
                if (response.isSuccessful && response.body() != null) {
                    _detailSuratPindah.postValue(response.body()?.data)
                } else {
                    _error.postValue("Gagal memuat detail surat: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                Log.e("SuratPindahViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun createSuratPindah(suratPindahRequest: CreateSuratPindahRequest) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.createSuratPindah(suratPindahRequest)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal membuat surat: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _operationResult.postValue(false)
                Log.e("SuratPindahViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateSuratPindah(id: Int, suratPindahRequest: CreateSuratPindahRequest) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.updateSuratPindah(id, suratPindahRequest)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal memperbarui surat: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _operationResult.postValue(false)
                Log.e("SuratPindahViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteSuratPindah(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.deleteSuratPindah(id)
                if (response.isSuccessful) {
                    _deleteResult.postValue(true)
                } else {
                    _error.postValue("Gagal menghapus surat: ${response.message()}")
                    _deleteResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _deleteResult.postValue(false)
                Log.e("SuratPindahViewModel", "Exception during delete: ${e.message}")
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
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

}

