package com.localclasstech.layanandesa.auth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.beranda.viewmodel.BerandaViewModelFactory

class LoginViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Set context di BerandaViewModelFactory
        BerandaViewModelFactory.setContext(context)

        return LoginViewModel(context) as T
    }
}