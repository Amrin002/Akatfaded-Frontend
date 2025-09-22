package com.localclasstech.layanandesa.feature.keluhan.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.feature.keluhan.data.network.KeluhanApiService
import com.localclasstech.layanandesa.feature.keluhan.data.repository.KeluhanRepository
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class KeluhanViewModelFactory (private val keluhanApiService: KeluhanApiService,
                               private val preferencesHelper: PreferencesHelper): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KeluhanViewModel::class.java)) {
            val repository = KeluhanRepository(keluhanApiService, preferencesHelper)
            return KeluhanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}