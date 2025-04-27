package com.localclasstech.layanandesa.feature.layanan.viewmodel.surat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class SuratDomisiliViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuratDomisiliViewModel::class.java)) {
            val apiService = RetrofitClient.suratApiService
            val preferencesHelper = PreferencesHelper(context)
            val repository = SuratDomisiliRepository(apiService, preferencesHelper)
            return SuratDomisiliViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}