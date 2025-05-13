package com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModelFactory
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.network.UserData
import com.localclasstech.layanandesa.settings.PreferencesHelper
import kotlinx.coroutines.launch
import java.io.File

class EditProfileViewModel(
    private val updateProfileRepository: UpdateProfileRepository,
    private val preferencesHelper: PreferencesHelper) : ViewModel() {
    private val _userProfile = MutableLiveData<UserData>()
    val userProfile: LiveData<UserData> get() = _userProfile

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchUserProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = updateProfileRepository.getUserProfile()

                if (response.isSuccessful) {
                    response.body()?.let { userData ->
                        preferencesHelper.updateUserProfile(userData.name, userData.image)
                        Log.d("EditProfileViewModel", "User Data: $userData")
                        _userProfile.postValue(userData)
                    } ?: run {
                        Log.e("EditProfileViewModel", "Data profil kosong")
                        _error.postValue("Data profil kosong")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditProfileViewModel", "Error Body: $errorBody")
                    _error.postValue("Gagal memuat profil: ${response.message()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("EditProfileViewModel", "Exception: ${e.message}", e)
                _error.postValue("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserProfile(
        name: String,
        email: String,
        nik: String,
        noTelp: String,
        password: String? = null,
        imageFile: File? = null
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = userProfile.value?.id ?: return@launch
                val response = updateProfileRepository.updateUserProfile(
                    userId, name, email, nik, noTelp, password, imageFile
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val updatedUser = body.user  // <-- ambil UserData di dalam wrapper
                        // simpan ke prefs
                        preferencesHelper.syncUserProfile(updatedUser.name, updatedUser.image)
                        // update live data
                        _userProfile.postValue(updatedUser)
                        _operationResult.postValue(true)
                        Log.d("EditProfileVM", "Profile updated: $updatedUser")
                    } else {
                        _error.postValue("Response kosong dari server")
                        _operationResult.postValue(false)
                    }
                } else {
                    val err = response.errorBody()?.string()
                    _error.postValue("Gagal: ${response.message()} – $err")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.localizedMessage}")
                _operationResult.postValue(false)
                Log.e("EditProfileVM", "Exception", e)
            } finally {
                _isLoading.value = false
            }
        }

    }

}