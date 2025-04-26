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

class SuratKtmViewModel(private val repository: SuratKtmRepository) : ViewModel() {
    private val _detailSuratKtm = MutableLiveData<SktmResponse>()
    val detailSuratKtm: LiveData<SktmResponse> get() = _detailSuratKtm

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

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
}