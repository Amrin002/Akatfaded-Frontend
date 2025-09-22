package com.localclasstech.layanandesa.view.getstarted

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GetStartedViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GetStartedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GetStartedViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}