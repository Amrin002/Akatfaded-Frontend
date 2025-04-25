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

    fun fetchSuratKtmDetail(id: Int) {
        viewModelScope.launch {
            try {
                // Menggunakan fungsi getDetailSuratKtmById untuk mendapatkan data surat berdasarkan id
                val response = repository.getDetailSuratKtmById(id)

                // Cek apakah response berhasil dan mengandung data yang valid
                if (response.isSuccessful && response.body() != null) {
                    _detailSuratKtm.postValue(response.body()?.data) // Ambil data dari response
                } else {
                    // Handle error jika response tidak berhasil
                    Log.e("SuratKtmViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception, misalnya show toast atau log error
                Log.e("SuratKtmViewModel", "Exception: ${e.message}")
            }
        }
    }
}
