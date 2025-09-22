package com.localclasstech.layanandesa.feature.keluhan.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.keluhan.data.repository.KeluhanRepository
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class DetailkeluhanViewModelFactory(private val context: Context): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailkeluhanViewModel::class.java)){
            val apiService = RetrofitClient.keluhanApiService
            val preferencesHelper = PreferencesHelper(context)
            val repository = KeluhanRepository(apiService, preferencesHelper)
            return DetailkeluhanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}