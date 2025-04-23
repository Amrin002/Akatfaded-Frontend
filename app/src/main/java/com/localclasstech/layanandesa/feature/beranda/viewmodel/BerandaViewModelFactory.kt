package com.localclasstech.layanandesa.feature.beranda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel

class BerandaViewModelFactory(private val loginViewModel: LoginViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BerandaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BerandaViewModel(loginViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
