package com.localclasstech.layanandesa.feature.apbdes.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.apbdes.data.repository.ApbdesRepository
import com.localclasstech.layanandesa.network.RetrofitClient

class ApbdesViewModelFactory(private val context: Context):ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApbdesViewModel::class.java)){
            val apiService = RetrofitClient.apbdesApiService
            val repository = ApbdesRepository(apiService)
            return ApbdesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")

    }
}