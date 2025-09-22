package com.localclasstech.layanandesa.feature.umkm.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.umkm.data.repository.UmkmRepository
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class KelolaUmkmViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KelolaUmkmViewModel::class.java)) {
            val apiService = RetrofitClient.umkmApiService // Pastikan ini ada di RetrofitClient
            val preferencesHelper = PreferencesHelper(context)
            val repository = UmkmRepository(apiService, preferencesHelper)
            return KelolaUmkmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}