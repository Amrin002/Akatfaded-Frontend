package com.localclasstech.layanandesa.feature.beranda.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModelFactory
import com.localclasstech.layanandesa.feature.berita.data.network.BeritaApiService
import com.localclasstech.layanandesa.feature.berita.data.repository.BeritaRepository
import com.localclasstech.layanandesa.network.RetrofitClient

class BerandaViewModelFactory(
    private val loginViewModel: LoginViewModel
) : ViewModelProvider.Factory {
    // Tambahkan companion object untuk menyimpan context
    companion object {
        private var appContext: Context? = null

        fun setContext(context: Context) {
            appContext = context.applicationContext
        }

        fun getContext(): Context {
            return appContext ?: throw IllegalStateException("Context not initialized")
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BerandaViewModel::class.java)) {
            // Buat BeritaRepository menggunakan apiService dari BeritaApiService
            val apiService = RetrofitClient.beritaApiService
            val context = appContext ?: throw IllegalStateException("Context not initialized")
            val beritaRepository = BeritaRepository(apiService, context)

            @Suppress("UNCHECKED_CAST")
            return BerandaViewModel(loginViewModel, beritaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}