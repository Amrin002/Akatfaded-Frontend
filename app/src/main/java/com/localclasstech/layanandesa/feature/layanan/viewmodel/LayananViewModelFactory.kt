package com.localclasstech.layanandesa.feature.layanan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.feature.beranda.viewmodel.BerandaViewModel
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository

class LayananViewModelFactory (private val suratKtmRepository: SuratKtmRepository,
                               private val suratDomisiliRepository: SuratDomisiliRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LayananViewModel::class.java)) {
            return LayananViewModel(suratKtmRepository, suratDomisiliRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}