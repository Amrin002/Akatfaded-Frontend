package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtuRepository
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class SuratKtuViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuratKtuViewModel::class.java)) {
            val apiService = RetrofitClient.suratApiService
            val preferencesHelper = PreferencesHelper(context)
            val repository = SuratKtuRepository(apiService, preferencesHelper)
            return SuratKtuViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}