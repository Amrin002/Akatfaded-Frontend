package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class SuratKtmViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuratKtmViewModel::class.java)) {
            val apiService = RetrofitClient.suratApiService
            val preferencesHelper = PreferencesHelper(context)
            val repository = SuratKtmRepository(apiService, preferencesHelper)
            return SuratKtmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
