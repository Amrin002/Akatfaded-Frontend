package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository

class SuratDomisiliViewModelFactory(private val repository: SuratDomisiliRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuratDomisiliViewModel::class.java)) {
            return SuratDomisiliViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}