package com.localclasstech.layanandesa.feature.layanan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.toCardSuratDomisili
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.toCardSuratKtm
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import kotlinx.coroutines.launch

class LayananViewModel(
    private val suratKtmRepository: SuratKtmRepository,
    private val suratDomisiliRepository: SuratDomisiliRepository
) : ViewModel() {

    private val _suratListKtm = MutableLiveData<List<DataClassCardSurat>>()
    val suratListKtm: LiveData<List<DataClassCardSurat>> = _suratListKtm

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchSuratKtmByUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = suratKtmRepository.getSuratKtmByUser()
                if (response.isSuccessful) {
                    val data = response.body()?.data.orEmpty()
                    val listMapped = data.map { it.toCardSuratKtm() }
                    _suratListKtm.postValue(listMapped)
                    _error.postValue(null)
                } else {
                    _error.postValue("Gagal memuat data: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Terjadi kesalahan: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
