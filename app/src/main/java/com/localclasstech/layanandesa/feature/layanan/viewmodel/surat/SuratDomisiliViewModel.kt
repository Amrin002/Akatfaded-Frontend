package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.SuratDomisiliResponse
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import kotlinx.coroutines.launch

class SuratDomisiliViewModel(private val repository: SuratDomisiliRepository) : ViewModel() {
    private val _detailSuratDomisili = MutableLiveData<SuratDomisiliResponse>()
    val detailSuratDomisili: LiveData<SuratDomisiliResponse> get() = _detailSuratDomisili

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    fun fetchSuratDomisiliDetail(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getDetailSuratDomisiliById(id)
                if (response.isSuccessful && response.body() != null) {
                    _detailSuratDomisili.postValue(response.body()?.data)
                } else {
                    _error.postValue("gagal Memuat Detail Surat: ${response.message()}")
                    }
                } catch (e: Exception) {
                    _error.postValue("Failed to fetch surat domisili detail: ${e.message}")
                }
            }
    }

    fun createSuratDomisili(request: CreateSuratDomisiliRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createSuratDomisili(request)
                if (response.isSuccessful && response.body() != null) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Failed to create surat domisili: ${response.message()}")
                }
                } catch (e: Exception) {
                    _error.postValue("Failed to create surat domisili: ${e.message}")
                _operationResult.postValue(false)
                }
        }
    }

    fun updateSuratDomisili(id: Int, request: CreateSuratDomisiliRequest) {
        viewModelScope.launch {
            try {
                val response = repository.updateSuratDomisili(id, request)
                if (response.isSuccessful && response.body() != null) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Failed to update surat domisili: ${response.message()}")
                }
                } catch (e: Exception) {
                    _error.postValue("Failed to update surat domisili: ${e.message}")
                _operationResult.postValue(false)
                Log.d("SuratDomisiliViewModel", "Error message: ${e.message}")
                }
        }
    }
}