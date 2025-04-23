package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.SuratDomisiliResponse
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import kotlinx.coroutines.launch

class SuratDomisiliViewModel(private val repository: SuratDomisiliRepository) : ViewModel() {
    private val _suratList = MutableLiveData<List<SuratDomisiliResponse>>()
    val suratList: LiveData<List<SuratDomisiliResponse>> get() = _suratList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun getAllSuratDomisili() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getAllSurat()
                if (response.isSuccessful) {
                    _suratList.value = response.body()?.data ?: emptyList()
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun createSuratDomisili(request: CreateSuratDomisiliRequest) {
        viewModelScope.launch {
            repository.createSurat(request)
            getAllSuratDomisili()
        }
    }
}