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
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu.toCardSuratKtu
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili.toCardSuratPindah
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtuRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratPindahRepository
import kotlinx.coroutines.launch

class LayananViewModel(
    private val suratKtmRepository: SuratKtmRepository,
    private val suratDomisiliRepository: SuratDomisiliRepository,
    private val suratPindahRepository: SuratPindahRepository,
    private val suratKtuRepository: SuratKtuRepository
) : ViewModel() {

    private val _suratListKtm = MutableLiveData<List<DataClassCardSurat>>()
    val suratListKtm: LiveData<List<DataClassCardSurat>> = _suratListKtm

    private val _suratListDomisili = MutableLiveData<List<DataClassCardSurat>>()
    val suratListDomisili: LiveData<List<DataClassCardSurat>> = _suratListDomisili

    private val _suratListPindah = MutableLiveData<List<DataClassCardSurat>>()
    val suratListPindah: LiveData<List<DataClassCardSurat>> = _suratListPindah

    private val _suratListKtu = MutableLiveData<List<DataClassCardSurat>>()
    val suratListKtu: LiveData<List<DataClassCardSurat>> = _suratListKtu

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchSuratKtmByUser(id : Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = suratKtmRepository.getSuratKtmByUser(id)
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
    fun fetchSuratDomisiliByUser(id : Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = suratDomisiliRepository.getSuratDomisiliByUser(id)
                if (response.isSuccessful){
                    val data = response.body()?.data.orEmpty()
                    val listMapped = data.map { it.toCardSuratDomisili() }
                    _suratListDomisili.postValue(listMapped)
                    _error.postValue(null)
                }else{
                    _error.postValue("Gagal memuat data: ${response.message()}")
                    Log.e("LayananViewModelDomisili", "Error fetching surat domisili: ${response.message()}")
                }
            }catch (e: Exception){
                _error.postValue("Terjadi kesalahan: ${e.localizedMessage}")
                Log.e("LayananViewModelDomisili", "Error fetching surat domisili: ${e.localizedMessage}")
            }finally {
            _isLoading.postValue(false)
            }
        }
    }
    // New function for Surat Pindah
    fun fetchSuratPindahByUser(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = suratPindahRepository.getSuratPindahByUser(id)
                if (response.isSuccessful) {
                    val data = response.body()?.data.orEmpty()
                    val listMapped = data.map { it.toCardSuratPindah() }
                    _suratListPindah.postValue(listMapped)
                    _error.postValue(null)
                } else {
                    _error.postValue("Gagal memuat data: ${response.message()}")
                    Log.e("LayananViewModelPindah", "Error fetching surat pindah: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Terjadi kesalahan: ${e.localizedMessage}")
                Log.e("LayananViewModelPindah", "Error fetching surat pindah: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // New function for Surat KTU
    fun fetchSuratKtuByUser(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = suratKtuRepository.getSuratKtuByUser(id)
                if (response.isSuccessful) {
                    val data = response.body()?.data.orEmpty()
                    val listMapped = data.map { it.toCardSuratKtu() }
                    _suratListKtu.postValue(listMapped)
                    _error.postValue(null)
                } else {
                    _error.postValue("Gagal memuat data: ${response.message()}")
                    Log.e("LayananViewModelKtu", "Error fetching surat ktu: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Terjadi kesalahan: ${e.localizedMessage}")
                Log.e("LayananViewModelKtu", "Error fetching surat ktu: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
