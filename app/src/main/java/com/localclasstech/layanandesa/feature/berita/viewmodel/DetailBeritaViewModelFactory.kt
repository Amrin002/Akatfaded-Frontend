package com.localclasstech.layanandesa.feature.berita.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.berita.data.repository.BeritaRepository
import com.localclasstech.layanandesa.network.RetrofitClient

class DetailBeritaViewModelFactory(private val context: Context

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailBeritaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val apiService = RetrofitClient.beritaApiService
            val repository = BeritaRepository(apiService, context)
            return DetailBeritaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}