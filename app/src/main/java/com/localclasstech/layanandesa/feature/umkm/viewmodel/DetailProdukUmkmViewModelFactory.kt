package com.localclasstech.layanandesa.feature.umkm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.umkm.data.network.UmkmApiService
import com.localclasstech.layanandesa.feature.umkm.data.repository.UmkmRepository
import com.localclasstech.layanandesa.settings.PreferencesHelper

class DetailProdukUmkmViewModelFactory(
    private val umkmApiService: UmkmApiService,
    private val preferencesHelper: PreferencesHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailProdukUmkmViewModel::class.java)) {
            val repository = UmkmRepository(umkmApiService, preferencesHelper)
            return DetailProdukUmkmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}