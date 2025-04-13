package com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.settings.PreferencesHelper

class EditProfileViewModelFactory(
    private val apiService: ApiService,
    private val preferencesHelper: PreferencesHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(apiService, preferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}