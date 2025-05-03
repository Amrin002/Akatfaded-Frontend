package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.CreateSktmRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.SktmResponse
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class SuratKtmViewModel(private val repository: SuratKtmRepository) : ViewModel() {
    private val _detailSuratKtm = MutableLiveData<SktmResponse>()
    val detailSuratKtm: LiveData<SktmResponse> get() = _detailSuratKtm

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _pdfDownloadResult = MutableLiveData<Pair<Boolean, ResponseBody?>>()
    val pdfDownloadResult: LiveData<Pair<Boolean, ResponseBody?>> get() = _pdfDownloadResult

    fun fetchSuratKtmDetail(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getDetailSuratKtmById(id)
                if (response.isSuccessful && response.body() != null) {
                    _detailSuratKtm.postValue(response.body()?.data)
                } else {
                    _error.postValue("Gagal memuat detail surat: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                Log.e("SuratKtmViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun createSuratKtm(suratKtmRequest: CreateSktmRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createSuratKtm(suratKtmRequest)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal membuat surat: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _operationResult.postValue(false)
                Log.e("SuratKtmViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun updateSuratKtm(id: Int, suratKtmRequest: CreateSktmRequest) {
        viewModelScope.launch {
            try {
                val response = repository.updateSuratKtm(id, suratKtmRequest)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal memperbarui surat: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _operationResult.postValue(false)
                Log.e("SuratKtmViewModel", "Exception: ${e.message}")
            }
        }
    }

    private val _downloadUrlResult = MutableLiveData<DownloadUrlResult>()
    val downloadUrlResult: LiveData<DownloadUrlResult> = _downloadUrlResult

    fun getDownloadUrl(id: Int) {
        viewModelScope.launch {
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
            }
        }
    }
    data class DownloadUrlResult(val success: Boolean, val downloadUrl: String?)
    fun exportPdfSuratKtm(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.exportPdfSuratKtm(id)

                if (response.isSuccessful && response.body() != null) {
                    _pdfDownloadResult.postValue(Pair(true, response.body()))
                } else {
                    _error.postValue("Gagal mengunduh PDF: ${response.message()}")
                    _pdfDownloadResult.postValue(Pair(false, null))
                    Log.e("SuratKtmViewModel", "PDF export failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
                _pdfDownloadResult.postValue(Pair(false, null))
                Log.e("SuratKtmViewModel", "Exception during PDF export: ${e.message}")
            }
        }
    }
}
