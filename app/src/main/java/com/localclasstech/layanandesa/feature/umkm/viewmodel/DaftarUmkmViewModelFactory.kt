package com.localclasstech.layanandesa.feature.umkm.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.umkm.data.repository.UmkmRepository
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class DaftarUmkmViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DaftarUmkmViewModel::class.java)) {
            val apiService = RetrofitClient.umkmApiService
            val preferencesHelper = PreferencesHelper(context)
            val repository = UmkmRepository(apiService, preferencesHelper)
            return DaftarUmkmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}