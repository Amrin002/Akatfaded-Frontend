package com.localclasstech.layanandesa.feature.pengaturan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.localclasstech.layanandesa.settings.PreferencesHelper

class PengaturanViewModel(private val preferencesHelper: PreferencesHelper) : ViewModel() {

    private val _isModernTheme = MutableLiveData<Boolean>()
    val isModernTheme: LiveData<Boolean> get() = _isModernTheme
    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    fun confirmLogout() {
        preferencesHelper.clearLogin()
        _logoutEvent.value = true
    }

    init {
        // Set nilai awal dari preferensi
        _isModernTheme.value = preferencesHelper.getThemePreference()
    }

    fun setTheme(isModern: Boolean) {
        preferencesHelper.saveThemePreference(isModern)
        _isModernTheme.value = isModern
    }

    fun toggleTheme() {
        val newTheme = !(_isModernTheme.value ?: true)
        _isModernTheme.value = newTheme
        // Simpan preferensi ke SharedPreferences
        preferencesHelper.saveThemePreference(newTheme)
    }
}