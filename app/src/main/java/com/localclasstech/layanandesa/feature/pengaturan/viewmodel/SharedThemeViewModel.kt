package com.localclasstech.layanandesa.feature.pengaturan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.localclasstech.layanandesa.settings.PreferencesHelper

class SharedThemeViewModel(private val preferencesHelper: PreferencesHelper): ViewModel() {
    private val _isModernTheme = MutableLiveData<Boolean>().apply {
        value = preferencesHelper.getThemePreference()
    }
    val isModernTheme: LiveData<Boolean> get() = _isModernTheme

    fun setTheme(isModern: Boolean) {
        _isModernTheme.value = isModern
        preferencesHelper.saveThemePreference(isModern)
    }

}