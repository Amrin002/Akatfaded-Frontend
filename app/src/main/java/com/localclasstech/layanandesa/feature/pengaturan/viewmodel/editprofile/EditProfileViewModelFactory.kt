package com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper
class EditProfileViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
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
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            val apiService = RetrofitClient.clientService
            val preferencesHelper = PreferencesHelper(context)
            val updateProfileRepository = UpdateProfileRepository(apiService, preferencesHelper)

            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(updateProfileRepository, preferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}