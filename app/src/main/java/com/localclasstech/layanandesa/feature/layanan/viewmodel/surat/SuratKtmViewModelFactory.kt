package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository

class SuratKtmViewModelFactory(private val repository: SuratKtmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuratKtmViewModel::class.java)) {
            return SuratKtmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
