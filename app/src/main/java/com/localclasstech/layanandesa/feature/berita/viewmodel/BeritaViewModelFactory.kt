package com.localclasstech.layanandesa.feature.berita.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.berita.data.repository.BeritaRepository
import com.localclasstech.layanandesa.network.RetrofitClient

class BeritaViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeritaViewModel::class.java)) {
            val apiService = RetrofitClient.beritaApiService
            val repository = BeritaRepository(apiService, context)
            return BeritaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
