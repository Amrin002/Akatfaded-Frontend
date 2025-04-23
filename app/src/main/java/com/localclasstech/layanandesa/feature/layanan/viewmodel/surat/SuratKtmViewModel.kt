package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.CreateSktmRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.SktmResponse
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import kotlinx.coroutines.launch

class SuratKtmViewModel(private val repository: SuratKtmRepository) : ViewModel() {
    private val _suratList = MutableLiveData<List<SktmResponse>>()
    val suratList: LiveData<List<SktmResponse>> get() = _suratList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun getAllSuratKtm() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getSuratKtmByUser() //Unresolved reference: repository
                if (response.isSuccessful) {
                    _suratList.value = response.body()?.data ?: emptyList()
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun createSurat(request: CreateSktmRequest) {
        viewModelScope.launch {
            repository.createSuratKtm(request) //Unresolved reference: repository
            getAllSuratKtm() // Refresh
        }
    }
}