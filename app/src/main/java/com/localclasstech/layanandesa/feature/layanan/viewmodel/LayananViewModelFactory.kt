package com.localclasstech.layanandesa.feature.layanan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.feature.beranda.viewmodel.BerandaViewModel
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtuRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratPindahRepository

class LayananViewModelFactory (private val suratKtmRepository: SuratKtmRepository,
                               private val suratDomisiliRepository: SuratDomisiliRepository,
                               private val suratPindahRepository: SuratPindahRepository,
                               private val suratKtuRepository: SuratKtuRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LayananViewModel::class.java)) {
            return LayananViewModel(suratKtmRepository, suratDomisiliRepository, suratPindahRepository, suratKtuRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}