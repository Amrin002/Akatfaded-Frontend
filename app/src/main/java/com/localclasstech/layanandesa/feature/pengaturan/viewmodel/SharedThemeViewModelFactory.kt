package com.localclasstech.layanandesa.feature.pengaturan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.settings.PreferencesHelper

class SharedThemeViewModelFactory(private val preferencesHelper: PreferencesHelper): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SharedThemeViewModel(preferencesHelper) as T
    }
}