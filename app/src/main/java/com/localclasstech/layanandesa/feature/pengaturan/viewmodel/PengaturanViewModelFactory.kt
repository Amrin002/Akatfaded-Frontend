package com.localclasstech.layanandesa.feature.pengaturan.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.settings.PreferencesHelper

class PengaturanViewModelFactory(
    private val preferencesHelper: PreferencesHelper,
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PengaturanViewModel::class.java)) {
            return PengaturanViewModel(preferencesHelper, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}