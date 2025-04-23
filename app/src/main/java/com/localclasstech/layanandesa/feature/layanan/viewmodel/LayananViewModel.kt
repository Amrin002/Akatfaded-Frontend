package com.localclasstech.layanandesa.feature.layanan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat
import com.localclasstech.layanandesa.feature.layanan.data.DataClassSuratFilter
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.toCardSuratDomisili
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.toCardSuratKtm
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import kotlinx.coroutines.launch


class LayananViewModel(private val suratKtmRepository: SuratKtmRepository,
                       private val suratDomisiliRepository: SuratDomisiliRepository
) : ViewModel() {
    private val _suratList = MutableLiveData<List<DataClassSuratFilter>>()
    val suratList: LiveData<List<DataClassSuratFilter>> = _suratList


    fun loadAllSuratUser() {
        viewModelScope.launch {
            try {
                val responseKtm = suratKtmRepository.getSuratKtmByUser()
                val responseDomisili = suratDomisiliRepository.getAllSurat()

                if (responseKtm.isSuccessful && responseDomisili.isSuccessful) {
                    val listKtm = responseKtm.body()?.data?.map { it.toCardSuratKtm() } ?: emptyList()
                    val listDomisili = responseDomisili.body()?.data?.map { it.toCardSuratDomisili() } ?: emptyList()

                    _suratList.value = listOf(
                        DataClassSuratFilter(
                            tipeSurat = "Surat Keterangan Tidak Mampu",
                            listSurat = listKtm
                        ),
                        DataClassSuratFilter(
                            tipeSurat = "Surat Domisili",
                            listSurat = listDomisili
                        )
                    )
                } else {
                    // bisa log error, atau kasih fallback dummy
                    Log.e("LayananViewModel", "Gagal fetch data")
                }
            } catch (e: Exception) {
                Log.e("LayananViewModel", "Gagal: ${e.message}")
            }
        }
    }


}